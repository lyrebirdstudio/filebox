package com.lyrebirdstudio.fileboxlib.downloader.client

internal data class DownloaderClientConfig(
    val connectionTimeoutInMillis: Long,
    val readTimeoutInMillis: Long
)