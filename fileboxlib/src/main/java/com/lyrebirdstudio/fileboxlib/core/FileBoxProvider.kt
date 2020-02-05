package com.lyrebirdstudio.fileboxlib.core

import android.content.Context

object FileBoxProvider {

    @JvmStatic
    fun newInstance(context: Context, fileBoxConfig: FileBoxConfig): FileBox {
        return FileBoxImpl(
            context.applicationContext,
            fileBoxConfig
        )
    }
}