package com.lyrebirdstudio.filebox

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lyrebirdstudio.fileboxlib.core.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("CheckResult")
    private fun singleDownload() {
        val fileBoxRequest = FileBoxRequest("https://url1.png")

        FileBoxProvider.newInstance(applicationContext, FileBoxConfig.createDefault())
            .get(fileBoxRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fileBoxResponse ->
                when (fileBoxResponse) {
                    is FileBoxResponse.Downloading -> {
                        val progress: Float = fileBoxResponse.progress
                        val ongoingRecord: Record = fileBoxResponse.record
                    }
                    is FileBoxResponse.Complete -> {
                        val savedRecord: Record = fileBoxResponse.record
                        val savedPath = fileBoxResponse.record.getReadableFilePath()
                    }
                    is FileBoxResponse.Error -> {
                        val savedRecord: Record = fileBoxResponse.record
                        val error = fileBoxResponse.throwable
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun singleDownloadWithConfig() {
        val fileBoxRequest = FileBoxRequest("https://images.unsplash.com/photo-1558981408-db0ecd8a1ee4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")

        val fileBoxConfig = FileBoxConfig.FileBoxConfigBuilder()
            .setCryptoType(CryptoType.CONCEAL)
            .setTTLInMillis(TimeUnit.DAYS.toMillis(7))
            .setDirectory(DirectoryType.CACHE)
            .setFolderName("MyPhotos")
            .build()

        FileBoxProvider.newInstance(applicationContext, fileBoxConfig)
            .get(fileBoxRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fileBoxResponse ->
                when (fileBoxResponse) {
                    is FileBoxResponse.Downloading -> {
                        val progress: Float = fileBoxResponse.progress
                        val ongoingRecord: Record = fileBoxResponse.record
                    }
                    is FileBoxResponse.Complete -> {
                        val savedRecord: Record = fileBoxResponse.record
                        val savedPath = fileBoxResponse.record.getReadableFilePath()
                    }
                    is FileBoxResponse.Error -> {
                        val savedRecord: Record = fileBoxResponse.record
                        val error = fileBoxResponse.throwable
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun multipleDownloadRequest() {
        val fileBoxMultipleRequest = FileBoxMultiRequest(
            arrayListOf(
                FileBoxRequest("https://url1.png"),
                FileBoxRequest("https://url2.zip"),
                FileBoxRequest("https://url3.json"),
                FileBoxRequest("https://url4.mp4")
            )
        )

        FileBoxProvider.newInstance(applicationContext, FileBoxConfig.createDefault())
            .get(fileBoxMultipleRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fileBoxResponse ->
                when (fileBoxResponse) {
                    is FileBoxMultiResponse.Downloading -> {
                        val progress = fileBoxResponse.progress
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                    }
                    is FileBoxMultiResponse.Complete -> {
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                        ongoingFileResponseList.forEach { it.record.getReadableFilePath() }
                    }
                    is FileBoxMultiResponse.Error -> {
                        val error = fileBoxResponse.throwable
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun multipleDownloadRequestWithConfig() {
        val fileBoxMultipleRequest = FileBoxMultiRequest(
            arrayListOf(
                FileBoxRequest("https://url1.png"),
                FileBoxRequest("https://url2.zip"),
                FileBoxRequest("https://url3.json"),
                FileBoxRequest("https://url4.mp4")
            )
        )

        val fileBoxConfig = FileBoxConfig.FileBoxConfigBuilder()
            .setCryptoType(CryptoType.CONCEAL)
            .setTTLInMillis(TimeUnit.DAYS.toMillis(7))
            .setDirectory(DirectoryType.EXTERNAL)
            .setFolderName("MyPhotos")
            .build()

        FileBoxProvider.newInstance(applicationContext, fileBoxConfig)
            .get(fileBoxMultipleRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fileBoxResponse ->
                when (fileBoxResponse) {
                    is FileBoxMultiResponse.Downloading -> {
                        val progress = fileBoxResponse.progress
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                    }
                    is FileBoxMultiResponse.Complete -> {
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                        ongoingFileResponseList.forEach { it.record.getReadableFilePath() }
                    }
                    is FileBoxMultiResponse.Error -> {
                        val error = fileBoxResponse.throwable
                        val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                    }
                }
            }
    }
}
