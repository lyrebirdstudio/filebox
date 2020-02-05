package com.lyrebirdstudio.fileboxlib.urlresolver

import com.lyrebirdstudio.fileboxlib.core.FileExtension
import com.lyrebirdstudio.fileboxlib.core.ResolvedUrlData
import java.math.BigInteger
import java.security.MessageDigest

internal class DefaultUrlResolver : UrlResolver {

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

private fun resolveFileNameFromURL(url: String): String {
    val startIndex = url.lastIndexOf("/")
    var fileName = url.substring(startIndex + 1)
    fileName = fileName.substring(0, fileName.lastIndexOf("."))
    return fileName
}

private fun resolveFileExtensionFromURL(url: String): FileExtension {
    val startIndex = url.lastIndexOf("/")
    val fileName = url.substring(startIndex + 1)
    val extensionText = fileName.substring(fileName.lastIndexOf("."))
    return FileExtension(extensionText)
}

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}