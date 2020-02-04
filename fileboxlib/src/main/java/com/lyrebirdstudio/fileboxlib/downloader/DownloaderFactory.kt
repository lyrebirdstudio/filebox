package com.lyrebirdstudio.fileboxlib.downloader

import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClient
import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClientConfigBuilder
import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClientFactory
import com.lyrebirdstudio.fileboxlib.security.Crypto

object DownloaderFactory {

    fun create(crypto: Crypto): Downloader {
        val downloaderClientConfig = DownloaderClientConfigBuilder.Builder().build()
        val downloaderClient = DownloaderClientFactory.create(downloaderClientConfig)
        val downloaderConfig =
            DownloaderConfig.createDefault()
        return createDefaultDownloader(
            downloaderClient,
            downloaderConfig,
            crypto
        )
    }

    private fun createDefaultDownloader(
        downloaderClient: DownloaderClient,
        downloaderConfig: DownloaderConfig,
        crypto: Crypto
    ): Downloader {
        return DownloaderImpl(
            downloaderClient,
            downloaderConfig,
            crypto
        )
    }
}