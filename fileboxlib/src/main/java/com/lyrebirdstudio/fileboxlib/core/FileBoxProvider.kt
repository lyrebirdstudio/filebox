package com.lyrebirdstudio.fileboxlib.core

import android.content.Context
import com.lyrebirdstudio.fileboxlib.core.FileBox
import com.lyrebirdstudio.fileboxlib.core.FileBoxConfig
import com.lyrebirdstudio.fileboxlib.core.FileBoxImpl

object FileBoxProvider {

    @JvmStatic
    fun newInstance(context: Context, fileBoxConfig: FileBoxConfig): FileBox {
        return FileBoxImpl(
            context.applicationContext,
            fileBoxConfig
        )
    }
}