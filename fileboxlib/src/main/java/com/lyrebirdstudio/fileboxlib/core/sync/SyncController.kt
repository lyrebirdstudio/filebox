package com.lyrebirdstudio.fileboxlib.core.sync

import com.lyrebirdstudio.fileboxlib.core.sync.model.InvalidDataState
import com.lyrebirdstudio.fileboxlib.core.Record
import com.lyrebirdstudio.fileboxlib.core.extensions.plusAssign
import com.lyrebirdstudio.fileboxlib.filesystem.FileController
import com.lyrebirdstudio.fileboxlib.recorder.Recorder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

class SyncController(private val recorder: Recorder, private val fileController: FileController) {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private var isInvalidFilesDeleted = false

    private var isOldRecordsDeleted = false

    fun sync() {
        when {
            disposable.isDisposed -> return
            else -> startSync()
        }
    }

    fun destroy() {
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

    fun isSyncCompleted(): Boolean {
        return isInvalidFilesDeleted && isOldRecordsDeleted
    }

    private fun startSync() {
        disposable += Single
            .zip(
                fetchInvalidFilesSource(),
                fetchInvalidRecordsSource(),
                InvalidDataProducerFunction()
            )
            .flatMapCompletable { deleteInvalidData(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { isInvalidFilesDeleted = true },
                { isInvalidFilesDeleted = true }
            )

        disposable += deleteOldRecords()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { isOldRecordsDeleted = true },
                { isOldRecordsDeleted = true }
            )
    }

    /**
     * Fetch invalid files from the directory.
     * If file exist but record doesn't have that info,
     * The file is means nothing. So we delete the file without record
     * info.
     *
     * This situation happens whe  user cancels the process while
     * file is downloading but not completed.
     */
    private fun fetchInvalidFilesSource(): Single<List<File>> {
        return Single
            .zip(
                recorder.readAll(),
                fileController.readAllFiles(),
                InvalidFilesCheckerFunction()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    /**
     * Fetch invalid records from database.
     * If file is deleted or broken, record should be deleted
     */
    private fun fetchInvalidRecordsSource(): Single<List<Record>> {
        return Single
            .zip(
                recorder.readAll(),
                fileController.readAllFiles(),
                InvalidRecordsCheckerFunction()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    /**
     * Delete the invalid records and files
     */
    private fun deleteInvalidData(invalidDataState: InvalidDataState): Completable {
        return recorder.delete(invalidDataState.invalidRecords)
            .andThen(fileController.deleteFiles(invalidDataState.invalidFiles))
            .subscribeOn(Schedulers.io())
    }

    /**
     * Deletes too old records from the record DB and file system.
     */
    private fun deleteOldRecords(): Completable {
        val currentTimeInMillis = Date().time
        return recorder.readAll()
            .toObservable()
            .flatMap { Observable.fromIterable(it) }
            .filter {
                val totalMillisPassed = currentTimeInMillis - it.lastReadDate
                val oldDataLiveMillis =
                    SyncConfigDefaults.tooOldDataLiveMillis()
                totalMillisPassed > oldDataLiveMillis
            }
            .flatMapCompletable { record ->
                recorder.delete(record)
                    .andThen(fileController.deleteFile(File(record.originalFilePath)))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }
}