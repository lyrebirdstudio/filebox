package com.lyrebirdstudio.fileboxlib.recorder.client

import com.lyrebirdstudio.fileboxlib.core.Record
import com.lyrebirdstudio.fileboxlib.recorder.Recorder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class RoomRecorder(
    private val mapper: RoomMapper,
    private val roomRecorderDao: RoomRecorderDao
) : Recorder {

    override fun create(record: Record): Completable {
        return Single.just(record)
            .map { mapper.mapTo(record) }
            .flatMapCompletable { roomRecorderDao.create(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun read(url: String): Single<Record> {
        return roomRecorderDao.recordCount(url)
            .flatMap {
                if (it > 0) {
                    roomRecorderDao.read(url).map { mapper.mapFrom(it) }
                } else {
                    Single.just(Record.empty())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun readAll(): Single<List<Record>> {
        return roomRecorderDao.readAll()
            .flatMap {
                Observable.fromIterable(it)
                    .map { mapper.mapFrom(it) }
                    .toList()
                    .subscribeOn(Schedulers.io())
            }
            .subscribeOn(Schedulers.io())
    }

    override fun delete(record: Record): Completable {
        return roomRecorderDao.delete(record.url)
            .subscribeOn(Schedulers.io())
    }

    override fun delete(records: List<Record>): Completable {
        val recordUrls = arrayListOf<String>()
        records.forEach { recordUrls.add(it.url) }

        return roomRecorderDao.delete(recordUrls)
            .subscribeOn(Schedulers.io())
    }

    override fun updateLastReadTime(url: String, lastReadTime: Long): Completable {
        return roomRecorderDao.recordCount(url)
            .flatMapCompletable {
                if (it > 0) {
                    roomRecorderDao.updateLastReadTime(url, lastReadTime)
                } else {
                    Completable.complete()
                }
            }
            .subscribeOn(Schedulers.io())
    }

}