package com.cwand.lib.ktx.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * @author : chunwei
 * @date : 2020/12/21
 * @description : 持久化存储-SharedPreferences
 *
 */
class PersistentCookieStore : CookieStore {
    override fun add(url: HttpUrl, cookies: List<Cookie?>) {
        TODO("Not yet implemented")
    }

    override fun get(url: HttpUrl): List<Cookie?> {
        TODO("Not yet implemented")
    }

    override fun getCookies(): List<Cookie?> {
        TODO("Not yet implemented")
    }

    override fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(): Boolean {
        TODO("Not yet implemented")
    }
}