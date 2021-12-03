package com.hjadal.portalis

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
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
    }
}