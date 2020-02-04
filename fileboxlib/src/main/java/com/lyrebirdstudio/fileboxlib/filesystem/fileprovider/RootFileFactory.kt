package com.lyrebirdstudio.fileboxlib.filesystem.fileprovider

import android.content.Context
import com.lyrebirdstudio.fileboxlib.core.DirectoryType

object RootFileFactory {

    fun create(
        appContext: Context,
        directoryType: DirectoryType
    ): RootFileProvider {
        return when (directoryType) {
            DirectoryType.EXTERNAL -> ExternalRootFileProvider(
                appContext
            )
            DirectoryType.CACHE -> CacheRootFileProvider(
                appContext
            )
        }
    }
}