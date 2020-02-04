package com.lyrebirdstudio.fileboxlib.core

import io.reactivex.Flowable
import io.reactivex.functions.Function

class FileBoxMultiResponseCombiner private constructor() :
    Function<Array<Any>, FileBoxMultiResponse> {

    override fun apply(t: Array<Any>): FileBoxMultiResponse {

        val fileBoxResponseList = arrayListOf<FileBoxResponse>()
        t.forEach { fileBoxResponseList.add(it as FileBoxResponse) }

        val status = getDownloadStatus(fileBoxResponseList)

        val progress = getAverageProgress(fileBoxResponseList)

        val error = getErrorIfExist(fileBoxResponseList)

        return when (status) {
            Status.SUCCESS -> FileBoxMultiResponse.Complete(fileBoxResponseList)
            Status.ERROR -> FileBoxMultiResponse.Error(fileBoxResponseList, error!!)
            Status.LOADING -> FileBoxMultiResponse.Downloading(fileBoxResponseList, progress)
        }
    }

    private fun getAverageProgress(fileBoxResponseList: List<FileBoxResponse>): Float {
        var totalProgress = 0f

        fileBoxResponseList.forEach {
            totalProgress += when (it) {
                is FileBoxResponse.Started -> 0f
                is FileBoxResponse.Downloading -> it.progress
                is FileBoxResponse.Complete -> 1f
                is FileBoxResponse.Error -> 1f
            }
        }

        return totalProgress / fileBoxResponseList.size.toFloat()
    }

    private fun getDownloadStatus(fileBoxResponseList: List<FileBoxResponse>): Status {
        val statusList = arrayListOf<Status>()
        fileBoxResponseList.forEach {
            val status = when (it) {
                is FileBoxResponse.Started -> Status.LOADING
                is FileBoxResponse.Downloading -> Status.LOADING
                is FileBoxResponse.Complete -> Status.SUCCESS
                is FileBoxResponse.Error -> Status.ERROR
            }
            statusList.add(status)
        }


        return when {
            statusList.any { it == Status.LOADING } -> Status.LOADING
            statusList.any { it == Status.ERROR } -> Status.ERROR
            else -> Status.SUCCESS
        }
    }

    private fun getErrorIfExist(fileBoxResponseList: List<FileBoxResponse>): Throwable? {
        var error: Throwable? = null
        fileBoxResponseList.forEach {
            if (it is FileBoxResponse.Error) {
                error = it.throwable
            }
        }
        return error
    }

    companion object {
        fun create(fileDownloadList: List<Flowable<FileBoxResponse>>): Flowable<FileBoxMultiResponse> {
            return Flowable.combineLatest(fileDownloadList, FileBoxMultiResponseCombiner())
        }
    }
}