package com.portalis.lib

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NetUtil {
    companion object {
        @Throws(IOException::class)
        fun run(url: String, callback: Callback) {
            val request: Request = Request.Builder()
                .url(url)
                .build()
            OkHttpClient().newCall(request).enqueue(callback)
        }

        @Throws(IOException::class)
        fun get(url: String): String? {
            val request: Request = Request.Builder()
                .url(url)
                .build()
            return OkHttpClient().newCall(request).execute().body?.string()
        }
    }
}