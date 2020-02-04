package com.lyrebirdstudio.fileboxlib.core.sync

import com.lyrebirdstudio.fileboxlib.core.Record
import io.reactivex.functions.BiFunction
import java.io.File

class InvalidFilesCheckerFunction : BiFunction<List<Record>, List<File>, List<File>> {

    override fun apply(records: List<Record>, files: List<File>): List<File> {
        val recordMap = hashMapOf<String, Record>()
        records.forEach { record -> recordMap[record.originalFilePath] = record }

        val invalidFiles = arrayListOf<File>()
        files.forEach { file ->
            val relatedRecord = recordMap[file.absolutePath]

            when {
                relatedRecord == null -> invalidFiles.add(file)
                isContentLengthUnknown(relatedRecord.fileTotalLength) -> return@forEach
                isContentLengthNotEquals(file, relatedRecord) -> invalidFiles.add(file)
            }
        }

        return invalidFiles
    }

    private fun isContentLengthNotEquals(file: File, record: Record): Boolean {
        return file.length() < record.fileTotalLength
    }

    private fun isContentLengthUnknown(contentLength: Long): Boolean {
        return contentLength == ContentLengthType.UNKNOWN.lengthValue
    }
}