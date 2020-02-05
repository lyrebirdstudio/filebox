package com.lyrebirdstudio.fileboxlib.urlresolver

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData


interface UrlResolver {

    fun resolve(url: String): ResolvedUrlData
}