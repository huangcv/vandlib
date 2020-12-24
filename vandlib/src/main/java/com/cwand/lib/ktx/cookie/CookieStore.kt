package com.cwand.lib.ktx.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * @author : chunwei
 * @date : 2020/12/21
 * @description :
 *
 */
interface CookieStore {
    fun add(url: HttpUrl, cookies: List<Cookie>)

    operator fun get(url: HttpUrl): List<Cookie>

    fun getCookies(): List<Cookie>

    fun remove(url: HttpUrl, cookie: Cookie): Boolean

    fun removeAll(): Boolean
}