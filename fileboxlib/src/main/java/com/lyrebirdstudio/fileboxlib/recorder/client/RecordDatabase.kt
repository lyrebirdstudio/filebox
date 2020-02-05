package com.lyrebirdstudio.fileboxlib.recorder.client

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecordEntity::class], version = 1, exportSchema = false)
internal abstract class RecordDatabase : RoomDatabase() {

    abstract fun recorderDao(): RoomRecorderDao
}