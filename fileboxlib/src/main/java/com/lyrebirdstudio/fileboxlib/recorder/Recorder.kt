package com.lyrebirdstudio.fileboxlib.recorder

import com.lyrebirdstudio.fileboxlib.core.Record
import io.reactivex.Completable
import io.reactivex.Single

interface Recorder {

    fun create(record: Record): Completable

    fun read(url: String): Single<Record>

    fun readAll(): Single<List<Record>>

    fun updateLastReadTime(url: String, lastReadTime: Long): Completable

    fun delete(record: Record): Completable

    fun delete(records: List<Record>): Completable
}