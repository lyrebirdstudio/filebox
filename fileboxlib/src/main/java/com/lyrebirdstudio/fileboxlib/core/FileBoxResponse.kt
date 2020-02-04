package com.lyrebirdstudio.fileboxlib.core

sealed class FileBoxResponse(open val record: Record) {
    class Started(override val record: Record) : FileBoxResponse(record)

    class Downloading(override val record: Record, val progress: Float) : FileBoxResponse(record)

    class Complete(override val record: Record) : FileBoxResponse(record)

    class Error(override val record: Record, val throwable: Throwable) : FileBoxResponse(record)
}