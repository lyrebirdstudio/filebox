package com.lyrebirdstudio.fileboxlib.filesystem.fileprovider

import java.io.File

interface RootFileProvider {

    fun getRootFile(folderName: String): File
}