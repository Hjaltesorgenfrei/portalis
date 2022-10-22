package com.portalis.app
import android.graphics.Bitmap

sealed interface ImageSource {
    fun getImage(): Any
}

class UrlImageSource(private val url: String) : ImageSource {
    override fun getImage(): Any {
        return url
    }
}

class BitmapImageSource(private val bitmap: Bitmap) : ImageSource {
    override fun getImage(): Any {
        return bitmap
    }
}