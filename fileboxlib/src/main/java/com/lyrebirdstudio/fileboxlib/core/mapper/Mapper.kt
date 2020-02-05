package com.lyrebirdstudio.fileboxlib.core.mapper

internal interface Mapper<in Input, out Output> {

    fun map(input: Input): Output
}