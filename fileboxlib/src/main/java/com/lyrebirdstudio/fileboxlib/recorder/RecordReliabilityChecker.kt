package com.lyrebirdstudio.fileboxlib.recorder

interface RecordReliabilityChecker<in I> {

    fun isReliable(record: I): Boolean
}