package com.lyrebirdstudio.fileboxlib.downloader

internal data class DownloaderConfig(val bufferSize: Int) {

    companion object {

        private const val DEFAULT_BUFFER_SIZE = 1024

        fun createDefault(): DownloaderConfig {
            return DownloaderConfig(DEFAULT_BUFFER_SIZE)
        }
    }
}