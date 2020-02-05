package com.lyrebirdstudio.fileboxlib.urlresolver

internal object UrlResolverFactory {

    fun create(): UrlResolver {
        return DefaultUrlResolver()
    }
}