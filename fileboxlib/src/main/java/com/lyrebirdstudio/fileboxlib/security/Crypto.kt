package com.lyrebirdstudio.fileboxlib.security

import com.lyrebirdstudio.fileboxlib.core.CryptoType
import io.reactivex.Flowable
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface Crypto {

    fun encrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess>

    fun decrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess>

    fun toEncryptedStream(outputStream: OutputStream): OutputStream

    fun toDecryptedStream(inputStream: InputStream): InputStream

    fun isInitialized(): Boolean

    fun getCryptoType(): CryptoType
}