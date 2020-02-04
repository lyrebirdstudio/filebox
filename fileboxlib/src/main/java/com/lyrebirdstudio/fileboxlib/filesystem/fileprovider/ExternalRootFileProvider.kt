package com.lyrebirdstudio.fileboxlib.filesystem.fileprovider

import android.content.Context
import java.io.File
import java.io.FileNotFoundException

open class ExternalRootFileProvider(private val appContext: Context) :
    RootFileProvider {

    override fun getRootFile(folderName: String): File {
        val externalFolder =
            appContext.getExternalFilesDir(null)
                ?: appContext.filesDir
                ?: appContext.cacheDir
                ?: throw FileNotFoundException("Can not access external files.")
        val destinationFolder = File(externalFolder, folderName)
        if (destinationFolder.exists().not()) {
            destinationFolder.mkdirs()
        }
        return destinationFolder
    }
}