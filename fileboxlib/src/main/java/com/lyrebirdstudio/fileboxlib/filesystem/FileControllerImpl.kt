package com.lyrebirdstudio.fileboxlib.filesystem

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData
import com.lyrebirdstudio.fileboxlib.filesystem.fileprovider.RootFileProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

internal class FileControllerImpl(
    private val rootFileProvider: RootFileProvider,
    private val folderName: String
) : FileController {

    override fun createFile(resolvedUrlData: ResolvedUrlData): File {
        val rootFile = rootFileProvider.getRootFile(folderName)
        return File(rootFile, resolvedUrlData.encodedFileName)
    }

    override fun readAllFiles(): Single<List<File>> {
        return Single.create {
            val folder = rootFileProvider.getRootFile(folderName)
            val files = folder.listFiles()
            it.onSuccess(files?.toList() ?: arrayListOf())
        }
    }

    override fun deleteFiles(files: List<File>): Completable {
        return Completable.create {
            files.forEach {
                if (it.exists()) {
                    it.delete()
                }
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteFile(file: File): Completable {
        return Completable.create {
            if (file.exists()) {
                file.delete()
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteAllFiles(): Completable {
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