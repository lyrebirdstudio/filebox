package com.lyrebirdstudio.fileboxlib.core

import java.util.concurrent.TimeUnit

object Defaults {

    fun directory(): DirectoryType = DirectoryType.EXTERNAL

    fun timeToLive(): Long = TimeUnit.DAYS.toMillis(7)

    fun cryptoType(): CryptoType = CryptoType.NONE

    fun folderName(): String = "file_box"
}