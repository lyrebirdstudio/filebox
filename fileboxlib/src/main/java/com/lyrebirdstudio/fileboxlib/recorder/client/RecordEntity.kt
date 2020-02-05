package com.lyrebirdstudio.fileboxlib.recorder.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_entity")
internal class RecordEntity(
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "encoded_file_name") val encodedFileName: String,
    @ColumnInfo(name = "file_extension") val fileExtension: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "last_read_at") val lastReadAt: Long,
    @ColumnInfo(name = "etag") val etag: String,
    @ColumnInfo(name = "file_total_length") val fileTotalLength: Long,
    @ColumnInfo(name = "crypto_type") val cryptoType: String
)