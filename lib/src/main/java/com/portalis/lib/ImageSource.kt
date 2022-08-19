package com.portalis.lib

sealed interface ImageSource {
    fun getImage(): Any
}

class UrlImageSource(private val url: String) : ImageSource {
    override fun getImage(): Any {
        return url
    }
}

class ByteArrayImageSource(val bytes: ByteArray) : ImageSource {
    override fun getImage(): Any {
        return bytes
    }
}