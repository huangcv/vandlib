package com.cwand.lib.ktx.utils

import android.util.Base64
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.LinkedHashMap

/**
 * @author : chunwei
 * @date : 2020/12/23
 * @description :签名功能
 *
 */

class SignUtils private constructor() {

    companion object {

        private const val ALGORITHM = "HmacSHA256"

        fun sign(params: Map<String, String>, secretKey: String): String {
            if (params.isEmpty() || secretKey.isEmpty()) {
                return ""
            }
            var signResult = ""
            val pBuilder = StringBuilder()
            //将所有参数进行拼接
            val entrySet = params.entries
            for (entry in entrySet) {
                pBuilder.append(entry.key.trim { it <= ' ' }).append("=")
                    .append(entry.value.trim { it <= ' ' }).append("&")
            }
            if (pBuilder.isNotEmpty()) {
                pBuilder.deleteCharAt(pBuilder.length - 1)
            }
            try {
                val mac = Mac.getInstance(ALGORITHM)
                val secret = SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), mac.algorithm)
                mac.init(secret)
                val hash = mac.doFinal(pBuilder.toString().toByteArray(charset("UTF-8")))
                signResult = Base64.encodeToString(hash, Base64.DEFAULT).trim { it <= ' ' }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return signResult
        }

        fun paramsSortByKey(originalMap: Map<String, String>): Map<String, String> {
            //TreeMap 会对Key进行指定排序
            val resultMap: MutableMap<String, String> = TreeMap<String, String>(MapKeyComparator())
            resultMap.putAll(originalMap)
            return resultMap
        }

        fun paramsSortByValue(originalMap: Map<String, String>): Map<String, String> {
            val sortedMap: LinkedHashMap<String, String> = LinkedHashMap()
            val entryList: List<Map.Entry<String, String>> = ArrayList(originalMap.entries)
            Collections.sort(entryList, MapValueComparator())
            val iterator = entryList.iterator()
            var tempEntry: Map.Entry<String, String>? = null
            while (iterator.hasNext()) {
                tempEntry = iterator.next()
                sortedMap[tempEntry.key] = tempEntry.value
            }
            return sortedMap
        }
    }

    internal class MapKeyComparator : Comparator<String> {
        override fun compare(o1: String, o2: String): Int {
            return o1.compareTo(o2)
        }
    }

    internal class MapValueComparator :
        Comparator<Map.Entry<String, String>> {
        override fun compare(
            o1: Map.Entry<String, String>,
            o2: Map.Entry<String, String>,
        ): Int {
            return o1.value.compareTo(o2.value)
        }
    }
}