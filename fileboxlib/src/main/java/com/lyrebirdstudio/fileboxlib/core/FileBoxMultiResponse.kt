package com.lyrebirdstudio.fileboxlib.core

sealed class FileBoxMultiResponse(open val fileBoxResponseList: List<FileBoxResponse>){

    class Downloading(override val fileBoxResponseList: List<FileBoxResponse>, val progress: Float) : FileBoxMultiResponse(fileBoxResponseList)

    class Complete(override val fileBoxResponseList: List<FileBoxResponse>) : FileBoxMultiResponse(fileBoxResponseList)

    class Error(override val fileBoxResponseList: List<FileBoxResponse>, val throwable: Throwable) : FileBoxMultiResponse(fileBoxResponseList)
}