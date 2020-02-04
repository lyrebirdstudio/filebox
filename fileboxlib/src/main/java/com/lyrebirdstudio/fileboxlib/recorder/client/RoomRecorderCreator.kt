package com.lyrebirdstudio.fileboxlib.recorder.client

import android.content.Context
import com.lyrebirdstudio.fileboxlib.recorder.Recorder

object RoomRecorderCreator {

    fun create(context: Context, recorderNameSuffix: String): Recorder {
        val roomMapper = RoomMapper()
        val recorderDatabaseProvider = RoomDatabaseProvider(
            context, recorderNameSuffix
        ).provideRoomDatabase()
        return RoomRecorder(
            mapper = roomMapper,
            roomRecorderDao = recorderDatabaseProvider.recorderDao()
        )
    }
}