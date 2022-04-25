package com.portalis.lib

import okhttp3.*
import java.io.IOException

class NetUtil {
    companion object {
        private val cookieJar = Cooker()
        private var client = OkHttpClient.Builder().cookieJar(cookieJar).build()

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

class Cooker : CookieJar {
    var savedCookies: MutableMap<String, List<Cookie>> = mutableMapOf()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        synchronized(savedCookies) {
            return savedCookies.getOrDefault(url.host, emptyList())
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(savedCookies) {
            val oldCookies = savedCookies.getOrDefault(url.host, emptyList())
            savedCookies[url.host] = oldCookies.filter { oldCookie ->
                cookies.all { cookie ->
                    oldCookie.name != cookie.name
                }
            }.plus(cookies)
        }
    }
}