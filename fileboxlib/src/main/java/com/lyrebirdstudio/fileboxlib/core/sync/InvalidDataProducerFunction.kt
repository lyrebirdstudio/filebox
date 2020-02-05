package com.lyrebirdstudio.fileboxlib.core.sync

import com.lyrebirdstudio.fileboxlib.core.Record
import com.lyrebirdstudio.fileboxlib.core.sync.model.InvalidDataState
import io.reactivex.functions.BiFunction
import java.io.File

internal class InvalidDataProducerFunction : BiFunction<List<File>, List<Record>, InvalidDataState> {

    override fun apply(t1: List<File>, t2: List<Record>): InvalidDataState {
        return InvalidDataState(t1, t2)
    }
}