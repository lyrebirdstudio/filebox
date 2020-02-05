package com.lyrebirdstudio.fileboxlib.urlresolver

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData


internal interface UrlResolver {

    fun resolve(url: String): ResolvedUrlData
}