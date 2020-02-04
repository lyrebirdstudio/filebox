package com.lyrebirdstudio.fileboxlib.error

interface ErrorHandler {
    fun handle(throwable: Throwable)
}