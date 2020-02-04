package com.lyrebirdstudio.fileboxlib.urlresolver

import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData

class DefaultUrlResolver : UrlResolver {

    override fun resolve(url: String): ResolvedUrlData {
        val fileName = resolveFileNameFromURL(url)
        val encodedFileName = url.md5()
        val fileExtension = resolveFileExtensionFromURL(url)

        return ResolvedUrlData(
                fileName = fileName,
                encodedFileName = encodedFileName,
                fileExtension = fileExtension,
                originalUrl = url
        )
    }
}