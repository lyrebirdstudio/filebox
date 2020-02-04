package com.lyrebirdstudio.fileboxlib.core

data class ResolvedUrlData(val fileName: String,
                           val encodedFileName: String,
                           val fileExtension: FileExtension,
                           val originalUrl: String)