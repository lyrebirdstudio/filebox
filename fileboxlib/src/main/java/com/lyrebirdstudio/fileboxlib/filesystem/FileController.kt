package com.lyrebirdstudio.fileboxlib.filesystem

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

internal interface FileController{

    fun createFile(resolvedUrlData: ResolvedUrlData): File

    fun readAllFiles(): Single<List<File>>

    fun deleteFiles(files: List<File>): Completable

    fun deleteFile(file: File): Completable

    fun deleteAllFiles(): Completable
}