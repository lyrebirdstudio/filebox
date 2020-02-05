package com.lyrebirdstudio.fileboxlib.core.sync

import java.util.concurrent.TimeUnit

internal object SyncConfigDefaults {

    fun tooOldDataLiveMillis(): Long {
        return TimeUnit.DAYS.toMillis(30)
    }
}