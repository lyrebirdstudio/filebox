package com.lyrebirdstudio.fileboxlib.downloader.client

import java.util.concurrent.TimeUnit

internal object Defaults {

    fun connectionTimeout(): Long {
        return TimeUnit.SECONDS.toMillis(60)
    }

    fun readTimeout(): Long {
        return TimeUnit.SECONDS.toMillis(60)
    }

}