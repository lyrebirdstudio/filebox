package com.lyrebirdstudio.fileboxlib.recorder

internal interface RecordReliabilityChecker<in I> {

    fun isReliable(record: I): Boolean
}