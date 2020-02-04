package com.lyrebirdstudio.fileboxlib.downloader.client

import java.io.InputStream

class DownloaderClientResponse(
    val request: DownloaderClientRequest,
    val inputStream: InputStream,
    val contentLenght: Long,
    val etag: String
)
