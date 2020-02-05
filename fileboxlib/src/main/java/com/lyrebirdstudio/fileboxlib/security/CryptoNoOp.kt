package com.lyrebirdstudio.fileboxlib.security

import com.lyrebirdstudio.fileboxlib.core.CryptoType
import io.reactivex.Flowable
import java.io.File
import java.io.InputStream
import java.io.OutputStream

internal class CryptoNoOp : Crypto {

    override fun encrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess> {
        return Flowable.just(CryptoProcess.complete(originFile))
    }

    override fun decrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess> {
        return Flowable.just(CryptoProcess.complete(originFile))
    }

    override fun toEncryptedStream(outputStream: OutputStream): OutputStream {
        return outputStream
    }

    override fun toDecryptedStream(inputStream: InputStream): InputStream {
        return inputStream
    }

    override fun isInitialized(): Boolean {
        return true
    }

    override fun getCryptoType(): CryptoType {
        return CryptoType.NONE
    }
}