package com.lyrebirdstudio.fileboxlib.filesystem.fileprovider

import android.content.Context
import java.io.File

open class CacheRootFileProvider(private val appContext: Context) :
    RootFileProvider {

    override fun getRootFile(folderName: String): File {
        val folder = File(appContext.cacheDir, folderName)
        if (folder.exists().not()) {
            folder.mkdirs()
        }
        return folder
    }
}