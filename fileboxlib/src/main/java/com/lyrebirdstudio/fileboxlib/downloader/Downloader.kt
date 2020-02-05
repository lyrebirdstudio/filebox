package com.lyrebirdstudio.fileboxlib.downloader

import io.reactivex.Flowable


internal interface Downloader {

    fun download(downloadRequest: DownloadRequest): Flowable<DownloadResponse>
}