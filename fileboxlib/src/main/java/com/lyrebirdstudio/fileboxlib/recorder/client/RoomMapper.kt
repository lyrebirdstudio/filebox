package com.lyrebirdstudio.fileboxlib.recorder.client

import com.lyrebirdstudio.fileboxlib.core.CryptoType
import com.lyrebirdstudio.fileboxlib.core.Record
import com.lyrebirdstudio.fileboxlib.recorder.RecordMapper

internal class RoomMapper : RecordMapper<RecordEntity> {

    override fun mapTo(record: Record): RecordEntity {
        return RecordEntity(
            url = record.url,
            fileName = record.fileName,
            encodedFileName = record.encodedFileName,
            fileExtension = record.fileExtension,
            filePath = record.originalFilePath,
            createdAt = record.createdDate,
            lastReadAt = record.lastReadDate,
            etag = record.etag,
            fileTotalLength = record.fileTotalLength,
            cryptoType = record.cryptoType.typeName
        )
    }

    override fun mapFrom(mappedRecord: RecordEntity): Record {
        return Record(
            url = mappedRecord.url,
            originalFilePath = mappedRecord.filePath,
            fileName = mappedRecord.fileName,
            encodedFileName = mappedRecord.encodedFileName,
            fileExtension = mappedRecord.fileExtension,
            createdDate = mappedRecord.createdAt,
            lastReadDate = mappedRecord.lastReadAt,
            etag = mappedRecord.etag,
            fileTotalLength = mappedRecord.fileTotalLength,
            cryptoType = CryptoType.fromTypeName(mappedRecord.cryptoType)
        )
    }
}