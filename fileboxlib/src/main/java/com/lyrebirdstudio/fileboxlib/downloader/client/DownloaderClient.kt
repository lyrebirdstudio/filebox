package com.lyrebirdstudio.fileboxlib.downloader.client

import io.reactivex.Single

internal interface DownloaderClient {

    fun execute(downloaderClientRequest: DownloaderClientRequest): Single<DownloaderClientResponse>

}