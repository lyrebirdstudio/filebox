package com.lyrebirdstudio.fileboxlib.downloader

import com.lyrebirdstudio.fileboxlib.core.Record

internal sealed class DownloadResponse(open val record: Record) {

    class Started(
        override val record: Record,
        val totalBytesRead: Long,
        val totalFileLenght: Long
    ) : DownloadResponse(record)

    class Downloading(
        override val record: Record,
        val totalBytesRead: Long,
        val totalFileLenght: Long
    ) : DownloadResponse(record)

    class Completed(
        override val record: Record,
        val totalBytesRead: Long,
        val totalFileLenght: Long,
        val etag: String
    ) : DownloadResponse(record)

    class Error(
        override val record: Record,
        val error: Throwable
    ) : DownloadResponse(record)
}