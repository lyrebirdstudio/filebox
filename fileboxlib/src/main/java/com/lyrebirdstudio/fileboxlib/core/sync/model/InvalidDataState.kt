package com.lyrebirdstudio.fileboxlib.core.sync.model

import com.lyrebirdstudio.fileboxlib.core.Record
import java.io.File

internal data class InvalidDataState(val invalidFiles: List<File>, val invalidRecords: List<Record>)