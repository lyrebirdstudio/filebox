package com.lyrebirdstudio.fileboxlib.recorder.client

import com.lyrebirdstudio.fileboxlib.core.Record
import com.lyrebirdstudio.fileboxlib.recorder.RecordReliabilityChecker
import java.util.*

class ReliabilityCheckerImpl(private val timeToLiveInMillis: Long) :
    RecordReliabilityChecker<Record> {

    override fun isReliable(record: Record): Boolean {
        if (record.isEmpty()) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = record.createdDate
        calendar.add(Calendar.SECOND, (timeToLiveInMillis / 1000L).toInt())
        val expireDate = calendar.time
        val nowDate = Calendar.getInstance().time
        return nowDate.after(expireDate).not()
    }
}