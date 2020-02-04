package com.lyrebirdstudio.fileboxlib.core

import com.lyrebirdstudio.fileboxlib.error.DefaultErrorHandler
import com.lyrebirdstudio.fileboxlib.error.ErrorHandler
import io.reactivex.Flowable
import io.reactivex.Single

interface FileBox {

    fun get(fileBoxRequest: FileBoxRequest): Flowable<FileBoxResponse>

    fun get(fileBoxMultiRequest: FileBoxMultiRequest): Flowable<FileBoxMultiResponse>

    fun isDownloaded(fileBoxRequest: FileBoxRequest): Single<Boolean>

    fun destroy()

    fun isDestroyed(): Boolean

    companion object {

        private var errorHandler: ErrorHandler = DefaultErrorHandler()

        fun initialize(errorHandler: ErrorHandler) {
            Companion.errorHandler = errorHandler
        }

        fun notifyError(throwable: Throwable) {
            errorHandler.handle(throwable)
        }
    }
}