package com.lyrebirdstudio.fileboxlib.downloader.client

import java.util.concurrent.TimeUnit

interface DownloaderClientConfigBuilder {

    fun build(): DownloaderClientConfig

    fun connectionTimeout(duration: Long, timeUnit: TimeUnit): DownloaderClientConfigBuilder

    fun readTimeout(duration: Long, timeUnit: TimeUnit): DownloaderClientConfigBuilder

    class Builder : DownloaderClientConfigBuilder {

        private var connectionTimeoutInMillis = Defaults.connectionTimeout()

        private var readTimeoutInMillis = Defaults.readTimeout()

        override fun connectionTimeout(
            duration: Long,
            timeUnit: TimeUnit
        ): DownloaderClientConfigBuilder {
            connectionTimeoutInMillis = timeUnit.toMillis(duration)
            return this
        }

        override fun readTimeout(
            duration: Long,
            timeUnit: TimeUnit
        ): DownloaderClientConfigBuilder {
            readTimeoutInMillis = timeUnit.toMillis(duration)
            return this
        }

        override fun build(): DownloaderClientConfig {
            return DownloaderClientConfig(
                connectionTimeoutInMillis = connectionTimeoutInMillis,
                readTimeoutInMillis = readTimeoutInMillis
            )
        }
    }
}
