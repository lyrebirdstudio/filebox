package com.lyrebirdstudio.fileboxlib.downloader

import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClient
import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClientRequest
import com.lyrebirdstudio.fileboxlib.security.Crypto
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.*

internal class DownloaderImpl(
    private val downloaderClient: DownloaderClient,
    private val downloaderConfig: DownloaderConfig,
    private val crypto: Crypto
) : Downloader {

    override fun download(downloadRequest: DownloadRequest): Flowable<DownloadResponse> {
        return Flowable.create({ emitter ->

            /**
             * Clone existing record with updated time
             */
            val downloadRecord = downloadRequest.record.copy(createdDate = Date().time)

            /**
             * Notify downloading started
             */
            emitter.onNext(
                DownloadResponse.Started(
                    downloadRecord,
                    0,
                    0
                )
            )

            downloaderClient.execute(
                DownloaderClientRequest(
                    downloadRequest.record.url
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { clientResult ->
                        try {

                            /**
                             * If same tag then return complete
                             */
                            if (isSameEtag(clientResult.etag, downloadRecord.etag)) {
                                try {
                                    clientResult.inputStream.close()
                                } catch (e: Exception) {

                                }

                                downloadRecord.updateTime()
                                emitter.onNext(
                                    DownloadResponse.Completed(
                                        downloadRecord,
                                        clientResult.contentLenght,
                                        clientResult.contentLenght,
                                        clientResult.etag
                                    )
                                )
                                emitter.onComplete()
                                return@subscribe
                            }

                            /**
                             * Update time, tag and total file length
                             */
                            downloadRecord.updateTime()
                            downloadRecord.updateEtag(clientResult.etag)
                            downloadRecord.updateTotalLength(clientResult.contentLenght)

                            /**
                             * Notify loading started
                             */
                            emitter.onNext(
                                DownloadResponse.Downloading(
                                    downloadRecord,
                                    0,
                                    clientResult.contentLenght
                                )
                            )

                            /**
                             * Open stream
                             */
                            val outputStream =
                                FileOutputStream(downloadRequest.record.originalFilePath)
                            val bufferedOutputStream = BufferedOutputStream(outputStream)
                            val encryptedOutputStream =
                                crypto.toEncryptedStream(bufferedOutputStream)

                            val bufferedInputStream =
                                BufferedInputStream(clientResult.inputStream)

                            val data = ByteArray(downloaderConfig.bufferSize)
                            var totalRead = 0L

                            /**
                             * Read from stream, write to file
                             */
                            while (true) {
                                val byteCount = bufferedInputStream.read(data)
                                if (byteCount < 0) break
                                encryptedOutputStream.write(data, 0, byteCount)
                                totalRead += byteCount


                                downloadRecord.updateTime()
                                emitter.onNext(
                                    DownloadResponse.Downloading(
                                        downloadRecord,
                                        totalRead,
                                        clientResult.contentLenght
                                    )
                                )
                            }

                            /**
                             * Close streams
                             */
                            bufferedInputStream.close()
                            encryptedOutputStream.flush()
                            encryptedOutputStream.close()

                            /**
                             * Update time and notify completed
                             */
                            downloadRecord.updateTime()
                            emitter.onNext(
                                DownloadResponse.Completed(
                                    downloadRecord,
                                    clientResult.contentLenght,
                                    clientResult.contentLenght,
                                    clientResult.etag
                                )
                            )
                            emitter.onComplete()
                        } catch (e: Exception) {
                            /**
                             * Update time and notify Error
                             */
                            downloadRecord.updateTime()
                            emitter.onNext(
                                DownloadResponse.Error(
                                    downloadRecord,
                                    e
                                )
                            )
                            emitter.onComplete()
                        }
                    },
                    {
                        /**
                         * Update time and notify Error
                         */
                        downloadRecord.updateTime()
                        emitter.onNext(
                            DownloadResponse.Error(
                                downloadRecord,
                                it
                            )
                        )
                        emitter.onComplete()
                    })

        }, BackpressureStrategy.BUFFER)
    }

    private fun isSameEtag(responseEtag: String, existingEtag: String): Boolean {
        return responseEtag.isNotEmpty() && responseEtag == existingEtag
    }
}