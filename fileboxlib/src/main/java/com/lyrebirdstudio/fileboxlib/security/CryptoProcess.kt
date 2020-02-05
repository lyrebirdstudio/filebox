package com.lyrebirdstudio.fileboxlib.security

import java.io.File

internal sealed class CryptoProcess {

    data class Processing(val percentage: Int) : CryptoProcess()

    data class Complete(val file: File) : CryptoProcess()

    data class Error(val file: File, val error: Throwable) : CryptoProcess()


    companion object {

        fun processing(percentage: Int): CryptoProcess = Processing(percentage = percentage)

        fun complete(file: File): CryptoProcess = Complete(file)

        fun error(file: File, error: Throwable): CryptoProcess = Error(file, error)
    }
}