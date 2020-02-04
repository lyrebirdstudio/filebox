package com.lyrebirdstudio.fileboxlib.security

import android.content.Context
import com.lyrebirdstudio.fileboxlib.core.CryptoType

object CryptoFactory {

    fun create(cryptoType: CryptoType, context: Context): Crypto {
        val crypto = when (cryptoType) {
            CryptoType.CONCEAL -> CryptoImpl(context)
            CryptoType.NONE -> CryptoNoOp()
        }

        return when {
            crypto.isInitialized().not() -> CryptoNoOp()
            else -> crypto
        }
    }
}