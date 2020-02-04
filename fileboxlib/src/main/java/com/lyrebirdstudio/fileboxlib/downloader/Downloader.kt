package com.lyrebirdstudio.fileboxlib.downloader

import io.reactivex.Flowable


interface Downloader {

    fun download(downloadRequest: DownloadRequest): Flowable<DownloadResponse>
}