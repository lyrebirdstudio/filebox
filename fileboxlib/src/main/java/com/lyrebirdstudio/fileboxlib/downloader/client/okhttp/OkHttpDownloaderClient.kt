package com.lyrebirdstudio.fileboxlib.downloader.client.okhttp

import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClient
import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClientRequest
import com.lyrebirdstudio.fileboxlib.downloader.client.DownloaderClientResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class OkHttpDownloaderClient(private val okHttpClient: OkHttpClient) : DownloaderClient {

    override fun execute(downloaderClientRequest: DownloaderClientRequest): Single<DownloaderClientResponse> {
        return Single.create {
            try {
                val request = Request.Builder().url(downloaderClientRequest.url).build()
                val response = okHttpClient.newCall(request).execute()
                val byteStream = response.body()?.byteStream()
                val contentLength = response.body()?.contentLength() ?: 0L
                val etag = getETag(response) ?: ""


                if (response.isSuccessful) {
                    it.onSuccess(
                        DownloaderClientResponse(
                            downloaderClientRequest,
                            byteStream!!,
                            contentLength,
                            etag
                        )
                    )
                } else {
                    it.onError(IOException(response.message()))
                }
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    private fun getETag(response: Response): String? {
        return response.headers().get("ETag")
    }
}