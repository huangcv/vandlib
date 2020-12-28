package com.cwand.lib.ktx.cookie

import com.cwand.lib.ktx.extensions.notNull
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

/**
 * @author : chunwei
 * @date : 2020/12/21
 * @description : 内存缓存-Cookie
 *
 */
class MemoryCookieStore : CookieStore {

    private val allCookies = HashMap<String, MutableList<Cookie>>()

    override fun add(url: HttpUrl, cookies: List<Cookie>) {
        val oldCookies = allCookies[url.host]
        oldCookies.notNull({
            val newIteration = cookies.iterator()
            val oldIteration = it.iterator()
            while (newIteration.hasNext()) {
                val newName = newIteration.next().name
                while (oldIteration.hasNext()) {
                    val oldName = oldIteration.next().name
                    if (newName == oldName) {
                        oldIteration.remove()
                    }
                }
            }
            it.addAll(cookies)
        }, {
            allCookies[url.host] = cookies.toMutableList()
        })
    }

    override fun get(url: HttpUrl): List<Cookie> {
        var mutableList = allCookies[url.host]
        if (mutableList == null) {
            mutableList = mutableListOf()
            allCookies[url.host] = mutableList
        }
        return mutableList
    }

    override fun getCookies(): List<Cookie> {
        val cookies: MutableList<Cookie> = mutableListOf()
        val httpUrls: MutableSet<String> = allCookies.keys
        for (httpUrl in httpUrls) {
            val elements = allCookies[httpUrl]
            elements?.let {
                cookies.addAll(it)
            }
        }
        return cookies
    }

    override fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        return allCookies[url.host]?.remove(cookie) ?: false
    }

    override fun removeAll(): Boolean {
        allCookies.clear()
        return true
    }
}