package com.lyrebirdstudio.fileboxlib.core

import android.annotation.SuppressLint
import android.content.Context
import com.lyrebirdstudio.fileboxlib.core.mapper.DownloadToFileBoxResponseMapper
import com.lyrebirdstudio.fileboxlib.core.sync.SyncController
import com.lyrebirdstudio.fileboxlib.core.extensions.plusAssign
import com.lyrebirdstudio.fileboxlib.filesystem.FileControllerFactory
import com.lyrebirdstudio.fileboxlib.recorder.Recorder
import com.lyrebirdstudio.fileboxlib.recorder.client.ReliabilityCheckerImpl
import com.lyrebirdstudio.fileboxlib.recorder.client.RoomRecorderCreator
import com.lyrebirdstudio.fileboxlib.urlresolver.UrlResolver
import com.lyrebirdstudio.fileboxlib.urlresolver.UrlResolverFactory
import com.lyrebirdstudio.fileboxlib.downloader.DownloadRequest
import com.lyrebirdstudio.fileboxlib.downloader.DownloadResponse
import com.lyrebirdstudio.fileboxlib.downloader.Downloader
import com.lyrebirdstudio.fileboxlib.downloader.DownloaderFactory
import com.lyrebirdstudio.fileboxlib.security.Crypto
import com.lyrebirdstudio.fileboxlib.security.CryptoFactory
import com.lyrebirdstudio.fileboxlib.security.CryptoProcess
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

internal class FileBoxImpl(context: Context, fileBoxConfig: FileBoxConfig) :
    FileBox {

    /**
     * Ensure if app context
     */
    private val appContext: Context = context.applicationContext

    /**
     * Check if data is reliable
     */
    private val reliabilityChecker =
        ReliabilityCheckerImpl(fileBoxConfig.timeToLiveInMillis)

    /**
     * Resolve Url to file name and extension
     */
    private val urlResolver: UrlResolver = UrlResolverFactory.create()

    /**
     * Record downloaded data.
     */
    private val recorder: Recorder =
        RoomRecorderCreator.create(appContext, fileBoxConfig.folderName)

    /**
     * Cryptography
     */
    private val crypto: Crypto = CryptoFactory.create(fileBoxConfig.cryptoType, appContext)

    /**
     * Downloads file
     */
    private val downloader: Downloader = DownloaderFactory.create(crypto)

    /**
     * Controller for file operations like create, delete.
     */
    private val fileController =
        FileControllerFactory.create(
            appContext,
            fileBoxConfig.directoryType,
            fileBoxConfig.folderName
        )

    /**
     * Controller for creating temporary files in cache
     * This controller is used for decrypt files to this destination
     */
    private val temporaryFileController = FileControllerFactory.createTemporaryDirectory(appContext)

    /**
     * Some of files and some of records will be invalid in some cases.
     * We launch syncer in initialize state to check invalid data and
     * deletes them.
     */
    private val syncController =
        SyncController(recorder, fileController)

    /**
     * Map downloaded response to file box response
     */
    private val fileBoxResponseMapper =
        DownloadToFileBoxResponseMapper()

    /**
     * Caches ongoing operations, emits latest state to subscribers
     */
    private val cacheSubject = hashMapOf<String, BehaviorSubject<FileBoxResponse>>()

    /**
     * Ongoing download operations will be added to disposable.
     * It will be cleared on destroy.
     */
    private var disposables = CompositeDisposable()

    init {
        syncController.sync()
    }

    /**
     * Downloads multiple file request concurrently. It calls get(fileRequest) method under the hood.
     * But it orchestrate all single request itself and return as a single response
     */
    override fun get(fileBoxMultiRequest: FileBoxMultiRequest): Flowable<FileBoxMultiResponse> {
        val downloadObservables = arrayListOf<Flowable<FileBoxResponse>>()

        fileBoxMultiRequest.fileBoxRequestList.forEach {
            downloadObservables.add(get(it))
        }

        return FileBoxMultiResponseCombiner.create(downloadObservables)
    }

    @SuppressLint("CheckResult")
    @Synchronized
    override fun get(fileBoxRequest: FileBoxRequest): Flowable<FileBoxResponse> {
        /**
         * Create disposable if already disposed
         */
        if (disposables.isDisposed) {
            disposables = CompositeDisposable()
        }

        if (fileBoxRequest.url.isEmpty()) {
            return Flowable.just(
                FileBoxResponse.Error(
                    Record.empty(),
                    IllegalArgumentException("Can not handle empty url")
                )
            )
        }

        /**
         * If already in cache return subject
         */
        if (cacheSubject.containsKey(fileBoxRequest.url)) {

            val cachedSubject = cacheSubject[fileBoxRequest.url]!!
            when (cachedSubject.value) {

                is FileBoxResponse.Started -> return getCachedFlowable(fileBoxRequest)

                is FileBoxResponse.Downloading -> return getCachedFlowable(fileBoxRequest)

                is FileBoxResponse.Complete -> return getCachedFlowable(fileBoxRequest)

                is FileBoxResponse.Error -> removeFromCache(fileBoxRequest)

                null -> return getCachedFlowable(fileBoxRequest)
            }
        }

        /**
         * Create subject
         */
        val cacheItem = BehaviorSubject.create<FileBoxResponse>()
        cacheSubject[fileBoxRequest.url] = cacheItem

        /**
         * Create new Record
         */
        val resolvedUrlData = urlResolver.resolve(fileBoxRequest.url)
        val destinationFile = fileController.createFile(resolvedUrlData)
        val decryptedFile = temporaryFileController.createFile(resolvedUrlData)

        /**
         * Read existing,
         * Publish existing record if it is reliable
         * Download and save to destination file If it is not reliable
         */
        disposables += recorder.read(fileBoxRequest.url)
            .flatMapPublisher { existingRecord ->
                if (reliabilityChecker.isReliable(existingRecord)) {
                    recorder.updateLastReadTime(fileBoxRequest.url, Date().time)
                        .andThen(decryptExistingRecord(existingRecord, decryptedFile))
                } else {
                    val newRecord =
                        if (existingRecord.isEmpty()) {
                            Record(
                                url = fileBoxRequest.url,
                                originalFilePath = destinationFile.absolutePath,
                                fileName = resolvedUrlData.fileName,
                                encodedFileName = resolvedUrlData.encodedFileName,
                                fileExtension = resolvedUrlData.fileExtension.extensionText,
                                createdDate = Date().time,
                                lastReadDate = Date().time,
                                etag = "",
                                fileTotalLength = 0,
                                cryptoType = crypto.getCryptoType()
                            )
                        } else {
                            existingRecord
                        }

                    val downloadRequest = DownloadRequest(newRecord)
                    downloader.download(downloadRequest)
                        .flatMap { downloadResponse ->
                            when (downloadResponse) {
                                is DownloadResponse.Completed -> decryptFlowable(
                                    downloadResponse,
                                    decryptedFile
                                )
                                else -> Flowable.just(downloadResponse)
                            }
                        }
                        .doOnNext { createNewRecordOnComplete(it) }
                }
            }
            .map { fileBoxResponseMapper.map(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    cacheItem.onNext(it)

                    if (it is FileBoxResponse.Error) {
                        FileBox.notifyError(it.throwable)
                    }
                },
                { FileBox.notifyError(it) })


        /**
         * Return subject to caller
         */
        return getCachedFlowable(fileBoxRequest)
    }

    /**
     * Check if file is downloaded
     */
    override fun isDownloaded(fileBoxRequest: FileBoxRequest): Single<Boolean> {
        return Single.create { emitter ->
            if (cacheSubject[fileBoxRequest.url]?.hasValue() == true && cacheSubject[fileBoxRequest.url]?.value is FileBoxResponse.Complete) {
                emitter.onSuccess(true)
            }

            recorder.read(fileBoxRequest.url)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { emitter.onSuccess(it.isEmpty().not()) },
                    { emitter.onSuccess(false) })
        }
    }

    /**
     * Creates new record when file downloaded and completed.
     */
    private fun createNewRecordOnComplete(downloadResponse: DownloadResponse) {
        if (downloadResponse is DownloadResponse.Completed) {
            disposables += recorder
                .create(downloadResponse.record)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {},
                    { FileBox.notifyError(it) })
        }
    }

    /**
     * Provides existing observable from the in-memory cache.
     */
    private fun getCachedFlowable(fileBoxRequest: FileBoxRequest): Flowable<FileBoxResponse> {
        return cacheSubject[fileBoxRequest.url]!!.toFlowable(BackpressureStrategy.LATEST)
    }

    /**
     * Remove item from in-memory cache
     */
    private fun removeFromCache(fileBoxRequest: FileBoxRequest) {
        cacheSubject[fileBoxRequest.url]?.onComplete()
        cacheSubject.remove(fileBoxRequest.url)
    }

    /**
     * Decrypt existing record
     */
    private fun decryptExistingRecord(
        existingRecord: Record,
        decryptedFile: File
    ): Flowable<DownloadResponse> {
        return Flowable
            .just(
                DownloadResponse.Completed(
                    existingRecord,
                    existingRecord.fileTotalLength,
                    existingRecord.fileTotalLength,
                    existingRecord.etag
                )
            )
            .flatMap { downloadResponse -> decryptFlowable(downloadResponse, decryptedFile) }
    }

    /**
     * Decrypt completed download response.
     * If CryptoFactory can not provide the instance with file's crypto type,
     * emits error to the flowable.
     */
    private fun decryptFlowable(
        downloadResponse: DownloadResponse.Completed,
        decryptedFile: File
    ): Flowable<DownloadResponse> {
        val crypto = CryptoFactory.create(downloadResponse.record.cryptoType, appContext)
        if (crypto.getCryptoType() != downloadResponse.record.cryptoType) {
            return Flowable.just(
                DownloadResponse.Error(
                    downloadResponse.record,
                    IllegalStateException("File is encrypted but conceal couldn't be initialized.")
                )
            )
        } else {
            return crypto
                .decrypt(File(downloadResponse.record.originalFilePath), decryptedFile)
                .filter { it is CryptoProcess.Complete || it is CryptoProcess.Error }
                .map {
                    when (it) {
                        is CryptoProcess.Complete -> {
                            downloadResponse.record.decryptedFilePath = it.file.absolutePath
                            downloadResponse
                        }
                        is CryptoProcess.Error -> DownloadResponse.Error(
                            downloadResponse.record,
                            it.error
                        )
                        else -> DownloadResponse.Error(
                            downloadResponse.record,
                            IllegalStateException("Undefined case in decryption process")
                        )
                    }
                }
        }
    }

    /**
     * Destroys ongoing disposables.
     * Clears in memory cache.
     * Clears internal cache folder path (Decrypted files)
     */
    override fun destroy() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }

        temporaryFileController.deleteAllFiles().subscribe()
        cacheSubject.forEach { it.value.onComplete() }
        cacheSubject.clear()

        syncController.destroy()
    }

    override fun isDestroyed(): Boolean {
        return disposables.isDisposed
    }
}