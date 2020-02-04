package com.lyrebirdstudio.fileboxlib.urlresolver

object UrlResolverFactory {

    fun create(): UrlResolver {
        return DefaultUrlResolver()
    }
}