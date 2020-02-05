package com.lyrebirdstudio.fileboxlib.filesystem.fileprovider

import java.io.File

internal interface RootFileProvider {

    fun getRootFile(folderName: String): File
}