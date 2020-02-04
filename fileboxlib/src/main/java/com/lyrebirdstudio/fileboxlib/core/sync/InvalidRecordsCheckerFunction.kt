package com.lyrebirdstudio.fileboxlib.core.sync

import com.lyrebirdstudio.fileboxlib.core.Record
import io.reactivex.functions.BiFunction
import java.io.File

class InvalidRecordsCheckerFunction : BiFunction<List<Record>, List<File>, List<Record>> {

    override fun apply(records: List<Record>, files: List<File>): List<Record> {

        val filesMap = hashMapOf<String, File>()
        files.forEach { filesMap[it.absolutePath] = it }

        val invalidRecords = arrayListOf<Record>()
        records.forEach { record ->
            val relatedFile = filesMap[record.originalFilePath]

            when {
                relatedFile == null -> invalidRecords.add(record)
                isContentLengthUnknown(record.fileTotalLength) -> return@forEach
                isContentLengthNotEquals(relatedFile, record) -> invalidRecords.add(record)
            }
        }
        return invalidRecords
    }

    private fun isContentLengthNotEquals(file: File, record: Record): Boolean {
        return file.length() < record.fileTotalLength
    }

    private fun isContentLengthUnknown(contentLength: Long): Boolean {
        return contentLength == ContentLengthType.UNKNOWN.lengthValue
    }
}