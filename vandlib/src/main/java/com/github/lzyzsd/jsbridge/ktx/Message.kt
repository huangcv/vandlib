package com.github.lzyzsd.jsbridge.ktx

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*

/**
 * @author : chunwei
 * @date : 2021/2/4
 * @description :
 *
 */
data class Message(
    var callbackId: String? = null,
    var responseId: String? = null,
    var responseData: String? = null,
    var data: String? = null,
    var handlerName: String? = null
) {
    companion object {
        private const val CALLBACK_ID_STR = "callbackId"
        private const val RESPONSE_ID_STR = "responseId"
        private const val RESPONSE_DATA_STR = "responseData"
        private const val DATA_STR = "data"
        private const val HANDLER_NAME_STR = "handlerName"

        fun toArrayList(jsonStr: String?): List<Message> {
            val list: MutableList<Message> =
                ArrayList()
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val m = Message()
                    val jsonObject = jsonArray.getJSONObject(i)
                    m.handlerName =
                        if (jsonObject.has(HANDLER_NAME_STR)) jsonObject.getString(HANDLER_NAME_STR) else null
                    m.callbackId =
                        if (jsonObject.has(CALLBACK_ID_STR)) jsonObject.getString(CALLBACK_ID_STR) else null
                    m.responseData =
                        if (jsonObject.has(RESPONSE_DATA_STR)) jsonObject.getString(
                            RESPONSE_DATA_STR
                        ) else null
                    m.responseId =
                        if (jsonObject.has(RESPONSE_ID_STR)) jsonObject.getString(RESPONSE_ID_STR) else null
                    m.data = if (jsonObject.has(DATA_STR)) jsonObject.getString(DATA_STR) else null
                    list.add(m)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return list
        }
    }

    fun toJson(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(CALLBACK_ID_STR, callbackId)
            jsonObject.put(DATA_STR, data)
            jsonObject.put(HANDLER_NAME_STR, handlerName)
            val data: String? = responseData
            if (TextUtils.isEmpty(data)) {
                jsonObject.put(RESPONSE_DATA_STR, data)
            } else {
                jsonObject.put(
                    RESPONSE_DATA_STR,
                    JSONTokener(data).nextValue()
                )
            }
            jsonObject.put(RESPONSE_DATA_STR, responseData)
            jsonObject.put(RESPONSE_ID_STR, responseId)
            return jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    fun toObject(jsonStr: String?): Message {
        val m = Message()
        try {
            jsonStr?.let {
                val jsonObject = JSONObject(it)
                m.handlerName =
                    if (jsonObject.has(HANDLER_NAME_STR)) jsonObject.getString(HANDLER_NAME_STR) else null
                m.callbackId =
                    if (jsonObject.has(CALLBACK_ID_STR)) jsonObject.getString(CALLBACK_ID_STR) else null
                m.responseData =
                    if (jsonObject.has(RESPONSE_DATA_STR)) jsonObject.getString(RESPONSE_DATA_STR) else null
                m.responseId =
                    if (jsonObject.has(RESPONSE_ID_STR)) jsonObject.getString(RESPONSE_ID_STR) else null
                m.data = if (jsonObject.has(DATA_STR)) jsonObject.getString(DATA_STR) else null
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return m
    }

}