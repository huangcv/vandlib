package com.cwand.lib.ktx.cookie

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.and
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author : chunwei
 * @date : 2020/12/21
 * @description : 持久化存储-SharedPreferences
 *
 */
class PersistentCookieStore : CookieStore {

    private val LOG_TAG = "PersistentCookieStore"
    private val COOKIE_PREFS = "CookiePrefsFile"
    private val COOKIE_NAME_PREFIX = "cookie_"

    private val cookies: HashMap<String, ConcurrentHashMap<String, Cookie>>
    private val cookiePrefs: SharedPreferences

    constructor(context: Context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
        cookies = HashMap()
        // Load any previously stored cookies into the store
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            if (value as String? != null && !(value as String).startsWith(COOKIE_NAME_PREFIX)) {
                val cookieNames = TextUtils.split(value as String?, ",")
                for (name in cookieNames) {
                    val encodedCookie =
                        cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null)
                    if (encodedCookie != null) {
                        val decodedCookie: Cookie? = decodeCookie(encodedCookie)
                        if (decodedCookie != null) {
                            if (!cookies.containsKey(key)) cookies[key!!] = ConcurrentHashMap()
                            cookies[key]!![name] = decodedCookie
                        }
                    }
                }
            }
        }
    }

    override fun add(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            add(url, cookie)
        }
    }

    protected fun add(uri: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        if (cookie.persistent) {
            if (!cookies.containsKey(uri.host)) {
                cookies[uri.host] = ConcurrentHashMap()
            }
            cookies[uri.host]!![name] = cookie
        } else {
            if (cookies.containsKey(uri.host)) {
                cookies[uri.host]!!.remove(name)
            } else {
                return
            }
        }

        // Save cookie into persistent store
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(uri.host, TextUtils.join(",", cookies[uri.host]!!.keys))
        prefsWriter.putString(COOKIE_NAME_PREFIX + name,
            encodeCookie(SerializableHttpCookie(cookie)))
        prefsWriter.apply()
    }

    protected fun getCookieToken(cookie: Cookie): String {
        return cookie.name + cookie.domain
    }

    override fun get(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host)) {
            val cookies: Collection<Cookie> = cookies[url.host]!!.values
            for (cookie in cookies) {
                if (isCookieExpired(cookie)) {
                    remove(url, cookie)
                } else {
                    ret.add(cookie)
                }
            }
        }
        return ret
    }

    private fun isCookieExpired(cookie: Cookie): Boolean {
        return cookie.expiresAt < System.currentTimeMillis()
    }

    override fun getCookies(): List<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) ret.addAll(cookies[key]!!.values)
        return ret
    }

    override fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        return if (cookies.containsKey(url.host) && cookies[url.host]!!.containsKey(name)) {
            cookies[url.host]!!.remove(name)
            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name)
            }
            prefsWriter.putString(url.host, TextUtils.join(",", cookies[url.host]!!.keys))
            prefsWriter.apply()
            true
        } else {
            false
        }
    }

    override fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    protected fun decodeCookie(cookieString: String): Cookie? {
        val bytes: ByteArray = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableHttpCookie).getCookie()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }
        return cookie
    }

    protected fun encodeCookie(cookie: SerializableHttpCookie?): String? {
        if (cookie == null) return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected fun byteArrayToHexString(bytes: ByteArray): String? {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Int = element and 0xff
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(
                hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}