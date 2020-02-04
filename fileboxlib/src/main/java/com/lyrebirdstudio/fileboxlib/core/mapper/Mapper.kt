package com.lyrebirdstudio.fileboxlib.core.mapper

interface Mapper<in Input, out Output> {

    fun map(input: Input): Output
}