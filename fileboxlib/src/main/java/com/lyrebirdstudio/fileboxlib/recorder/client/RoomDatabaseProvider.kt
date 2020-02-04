package com.lyrebirdstudio.fileboxlib.recorder.client

import android.content.Context
import androidx.room.Room
import com.lyrebirdstudio.fileboxlib.BuildConfig

class RoomDatabaseProvider(
    private val appContext: Context,
    private val databaseNameSuffix: String
) {

    private val database =
        Room.databaseBuilder(appContext, RecordDatabase::class.java, getDBName()).build()

    fun provideRoomDatabase(): RecordDatabase = database

    private fun getDBName(): String {
        if (BuildConfig.DEBUG) {
            return appContext.packageName + DB_NAME_SUFFIX + "_" + databaseNameSuffix
        }
        return appContext.packageName + DB_NAME_SUFFIX
    }

    companion object {

        private const val DB_NAME_SUFFIX = "_box_db"
    }
}