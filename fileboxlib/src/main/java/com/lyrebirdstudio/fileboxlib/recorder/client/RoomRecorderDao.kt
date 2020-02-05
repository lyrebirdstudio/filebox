package com.lyrebirdstudio.fileboxlib.recorder.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
internal interface RoomRecorderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(recordEntity: RecordEntity): Completable

    @Query("DELETE FROM record_entity WHERE url = :url")
    fun delete(url: String): Completable

    @Query("DELETE FROM record_entity WHERE url in (:urls)")
    fun delete(urls: List<String>): Completable

    @Query("SELECT * from record_entity WHERE url = :url")
    fun read(url: String): Single<RecordEntity>

    @Query("UPDATE record_entity SET last_read_at=:lastReadAt WHERE url = :url")
    fun updateLastReadTime(url: String, lastReadAt: Long): Completable

    @Query("SELECT * from record_entity")
    fun readAll(): Single<List<RecordEntity>>

    @Query("SELECT COUNT(*) FROM record_entity WHERE url = :url")
    fun recordCount(url: String): Single<Int>

}