package com.lyrebirdstudio.fileboxlib.security

import android.content.Context
import com.facebook.crypto.Entity
import com.lyrebirdstudio.fileboxlib.core.CryptoType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.io.*
import java.lang.Exception

class CryptoImpl(context: Context) : Crypto {

    private val appContext: Context = context.applicationContext

    private val crypto = ConcealInitializer.initialize(appContext)

    override fun encrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess> {
        return Flowable.create(
            {
                it.onNext(CryptoProcess.processing(0))

                try {
                    val fileOutputStream = BufferedOutputStream(FileOutputStream(destinationFile))
                    val cryptoOutputStream =
                        crypto.getCipherOutputStream(fileOutputStream, Entity.create("entity_id"))

                    val inputFileStream = FileInputStream(originFile)

                    val available = inputFileStream.available()
                    val buf = ByteArray(1024)
                    var read: Int = inputFileStream.read(buf)
                    var totalRead = read

                    while (read > 0) {

                        cryptoOutputStream.write(buf, 0, read)
                        read = inputFileStream.read(buf)

                        totalRead += read
                        val percent = totalRead * 100 / available
                        it.onNext(CryptoProcess.processing(percent))
                    }

                    cryptoOutputStream.close()

                    it.onNext(CryptoProcess.complete(destinationFile))
                    it.onComplete()
                } catch (e: Exception) {
                    it.onNext(CryptoProcess.error(destinationFile, e))
                    it.onComplete()
                }
            },
            BackpressureStrategy.BUFFER
        )
    }

    override fun decrypt(originFile: File, destinationFile: File): Flowable<CryptoProcess> {
        return Flowable.create({
            it.onNext(CryptoProcess.processing(0))

            try {
                val fileOutputStream = FileOutputStream(destinationFile)

                val fileInputStream = FileInputStream(originFile)

                val fileCryptoInputStream =
                    crypto.getCipherInputStream(fileInputStream, Entity.create("entity_id"))

                val available = fileCryptoInputStream.available()
                val buffer = ByteArray(1024)
                var read: Int = fileCryptoInputStream.read(buffer)
                var totalRead = read

                while (read != -1) {
                    fileOutputStream.write(buffer, 0, read)
                    read = fileCryptoInputStream.read(buffer)

                    totalRead += read
                    val percent = totalRead * 100 / available
                    it.onNext(CryptoProcess.processing(percent))
                }

                fileCryptoInputStream.close()
                it.onNext(CryptoProcess.complete(destinationFile))
                it.onComplete()
            } catch (e: Exception) {
                it.onNext(CryptoProcess.error(destinationFile, e))
                it.onComplete()
            }
        }, BackpressureStrategy.BUFFER)
    }

    override fun toEncryptedStream(outputStream: OutputStream): OutputStream {
        return crypto.getCipherOutputStream(outputStream, Entity.create("entity_id"))
    }

    override fun toDecryptedStream(inputStream: InputStream): InputStream {
        return crypto.getCipherInputStream(inputStream, Entity.create("entity_id"))
    }

    override fun isInitialized(): Boolean {
        return ConcealInitializer.isInitialized()
    }

    override fun getCryptoType(): CryptoType {
        return CryptoType.CONCEAL
    }
}