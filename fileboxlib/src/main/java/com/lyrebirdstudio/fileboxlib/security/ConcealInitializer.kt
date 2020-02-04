package com.lyrebirdstudio.fileboxlib.security

import android.content.Context
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.soloader.SoLoader
import java.lang.Exception

object ConcealInitializer {

    private var isNativeLoaderInitialized = false

    private var crypto: Crypto? = null

    @Synchronized
    fun initialize(context: Context): Crypto {
        if (isNativeLoaderInitialized.not()) {
            isNativeLoaderInitialized = try {
                SoLoader.init(context, false)
                true
            } catch (e: Exception) {
                false
            }
        }

        if (crypto == null) {
            val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
            crypto = AndroidConceal.get().createDefaultCrypto(keyChain)
        }

        return crypto!!
    }

    fun isInitialized(): Boolean {
        return isNativeLoaderInitialized && crypto != null
    }
}