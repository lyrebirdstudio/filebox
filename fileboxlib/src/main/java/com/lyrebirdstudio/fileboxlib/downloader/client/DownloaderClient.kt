package com.lyrebirdstudio.fileboxlib.downloader.client

import io.reactivex.Single

interface DownloaderClient {

    fun execute(downloaderClientRequest: DownloaderClientRequest): Single<DownloaderClientResponse>

}