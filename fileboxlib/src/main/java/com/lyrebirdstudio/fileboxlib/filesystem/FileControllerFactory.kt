package com.lyrebirdstudio.fileboxlib.filesystem

import android.content.Context
import com.lyrebirdstudio.fileboxlib.core.DirectoryType
import com.lyrebirdstudio.fileboxlib.filesystem.fileprovider.RootFileFactory

object FileControllerFactory {

    private const val TEMPORARY_FILE_NAME = "temporary"

    fun create(
        context: Context,
        directoryType: DirectoryType,
        folderName: String
    ): FileController {
        val rootFileProvider = RootFileFactory.create(context, directoryType)
        return FileController(rootFileProvider, folderName)
    }

    fun createTemporaryDirectory(context: Context): FileController {
        val rootFileProvider = RootFileFactory.create(context, DirectoryType.CACHE)
        return FileController(rootFileProvider, TEMPORARY_FILE_NAME)
    }
}