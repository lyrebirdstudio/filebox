package com.lyrebirdstudio.fileboxlib.core

import java.util.*

data class Record(
    val url: String,
    val originalFilePath: String,
    val fileName: String,
    val encodedFileName: String,
    val fileExtension: String,
    var createdDate: Long,
    var lastReadDate: Long,
    var etag: String,
    var fileTotalLength: Long,
    val cryptoType: CryptoType,
    var decryptedFilePath: String? = null
) {
    fun isEmpty() = url.isEmpty()

    fun updateTime() {
        createdDate = Date().time
    }

    fun updateEtag(etag: String) {
        this.etag = etag
    }

    fun updateTotalLength(fileTotalLength: Long) {
        this.fileTotalLength = fileTotalLength
    }

    fun getReadableFilePath(): String? {
        return when (cryptoType) {
            CryptoType.NONE -> originalFilePath
            else -> decryptedFilePath
        }
    }

    companion object {

        fun empty() = Record(
            url = "",
            originalFilePath = "",
            fileName = "",
            encodedFileName = "",
            fileExtension = "",
            createdDate = 0L,
            lastReadDate = 0L,
            etag = "",
            fileTotalLength = 0,
            cryptoType = CryptoType.NONE
        )
    }

}