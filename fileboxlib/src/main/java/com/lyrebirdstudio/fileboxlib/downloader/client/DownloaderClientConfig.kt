package com.lyrebirdstudio.fileboxlib.downloader.client

data class DownloaderClientConfig(
    val connectionTimeoutInMillis: Long,
    val readTimeoutInMillis: Long
)