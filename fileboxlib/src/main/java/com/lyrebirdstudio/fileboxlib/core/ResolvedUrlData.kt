package com.lyrebirdstudio.fileboxlib.core

internal data class ResolvedUrlData(
    val fileName: String,
    val encodedFileName: String,
    val fileExtension: FileExtension,
    val originalUrl: String
)