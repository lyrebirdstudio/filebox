package com.lyrebirdstudio.fileboxlib.downloader.client

import com.lyrebirdstudio.fileboxlib.downloader.client.okhttp.OkHttpDownloaderClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal object DownloaderClientFactory {

    fun create(config: DownloaderClientConfig = defaultConfig()): DownloaderClient {
        return getOkHttpDownloaderClient(config)
    }

    private fun getOkHttpDownloaderClient(config: DownloaderClientConfig): DownloaderClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(config.connectionTimeoutInMillis, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutInMillis, TimeUnit.MILLISECONDS)
            .build()

        return OkHttpDownloaderClient(okHttpClient)
    }

    private fun defaultConfig() = DownloaderClientConfigBuilder.Builder().build()

}