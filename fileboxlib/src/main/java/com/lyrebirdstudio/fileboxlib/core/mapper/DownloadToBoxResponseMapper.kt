package com.lyrebirdstudio.fileboxlib.core.mapper

import com.lyrebirdstudio.fileboxlib.core.FileBoxResponse
import com.lyrebirdstudio.fileboxlib.downloader.DownloadResponse

class DownloadToFileBoxResponseMapper :
    Mapper<DownloadResponse, FileBoxResponse> {
    override fun map(input: DownloadResponse): FileBoxResponse {
        return when (input) {
            is DownloadResponse.Started -> FileBoxResponse.Started(input.record)

            is DownloadResponse.Downloading -> FileBoxResponse.Downloading(
                input.record,
                input.totalBytesRead.toFloat() / input.totalFileLenght.toFloat()
            )

            is DownloadResponse.Completed -> FileBoxResponse.Complete(input.record)

            is DownloadResponse.Error -> FileBoxResponse.Error(input.record, input.error)
        }
    }
}