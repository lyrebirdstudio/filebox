package com.lyrebirdstudio.fileboxlib.filesystem

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData
import com.lyrebirdstudio.fileboxlib.filesystem.fileprovider.RootFileProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.Exception

class FileController(
    private val rootFileProvider: RootFileProvider,
    private val folderName: String
) {

    fun createFile(resolvedUrlData: ResolvedUrlData): File {
        val rootFile = rootFileProvider.getRootFile(folderName)
        return File(rootFile, resolvedUrlData.encodedFileName)
    }

    fun readAllFiles(): Single<List<File>> {
        return Single.create {
            val folder = rootFileProvider.getRootFile(folderName)
            val files = folder.listFiles()
            it.onSuccess(files?.toList() ?: arrayListOf())
        }
    }

    fun deleteFiles(files: List<File>): Completable {
        return Completable.create {
            files.forEach {
                if (it.exists()) {
                    it.delete()
                }
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    fun deleteFile(file: File): Completable {
        return Completable.create {
            if (file.exists()) {
                file.delete()
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    fun deleteAllFiles(): Completable {
        return Completable.create {
            val folder = rootFileProvider.getRootFile(folderName)
            try {
                folder.listFiles()?.forEach {
                    if (it.exists()) {
                        it.delete()
                    }
                }
            } catch (e: Exception) {
            } finally {
                it.onComplete()
            }
        }.subscribeOn(Schedulers.io())
    }
}