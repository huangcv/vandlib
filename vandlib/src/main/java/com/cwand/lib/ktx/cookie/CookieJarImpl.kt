package com.cwand.lib.ktx.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * @author : chunwei
 * @date : 2020/12/23
 * @description :
 *
 */
class CookieJarImpl : CookieJar {

    private val cookieStore: CookieStore

    constructor(cookieStore: CookieStore) {
        this.cookieStore = cookieStore
    }

    fun getCookieStore(): CookieStore? {
        return cookieStore
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url]
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.add(url, cookies)
    }
}