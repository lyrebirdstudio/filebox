package com.lyrebirdstudio.fileboxlib.downloader.client

import com.lyrebirdstudio.fileboxlib.downloader.client.okhttp.OkhttpDownloaderClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object DownloaderClientFactory {

    fun create(config: DownloaderClientConfig = defaultConfig()): DownloaderClient {
        return getOkhttpDownloaderClient(
            config
        )
    }

    private fun getOkhttpDownloaderClient(config: DownloaderClientConfig): DownloaderClient {
        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(config.connectionTimeoutInMillis, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutInMillis, TimeUnit.MILLISECONDS)
            .build()
        return OkhttpDownloaderClient(
            okhttpClient
        )
    }

    private fun defaultConfig() = DownloaderClientConfigBuilder.Builder().build()

}