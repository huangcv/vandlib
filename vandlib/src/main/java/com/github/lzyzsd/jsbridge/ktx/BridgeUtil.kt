package com.github.lzyzsd.jsbridge.ktx

import android.content.Context
import android.webkit.WebView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author : chunwei
 * @date : 2021/1/28
 * @description :
 *
 */
class BridgeUtil private constructor() {


    companion object {
        val YY_OVERRIDE_SCHEMA = "yy://"
        val YY_RETURN_DATA =
            YY_OVERRIDE_SCHEMA + "return/" //格式为   yy://return/{function}/returncontent

        val YY_FETCH_QUEUE = YY_RETURN_DATA + "_fetchQueue/"
        val EMPTY_STR = ""
        val UNDERLINE_STR = "_"
        val SPLIT_MARK = "/"

        val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
        val JS_HANDLE_MESSAGE_FROM_JAVA =
            "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');"
        val JS_FETCH_QUEUE_FROM_JAVA =
            "javascript:WebViewJavascriptBridge._fetchQueue();"
        val JAVASCRIPT_STR = "javascript:"

        // 例子 javascript:WebViewJavascriptBridge._fetchQueue(); --> _fetchQueue
        @JvmStatic
        fun parseFunctionName(jsUrl: String): String {
            return jsUrl.replace("javascript:WebViewJavascriptBridge.", "")
                .replace("\\(.*\\);".toRegex(), "")
        }

        @JvmStatic
        fun getDataFromReturnUrl(url: String): String? {
            if (url.startsWith(YY_FETCH_QUEUE)) {
                // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
                return url.replace(
                    YY_FETCH_QUEUE,
                    EMPTY_STR
                )
            }
            // temp = _fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
            val temp = url.replace(
                YY_RETURN_DATA,
                EMPTY_STR
            )
            val functionAndData =
                temp.split(SPLIT_MARK.toRegex())
                    .toTypedArray()
            if (functionAndData.size >= 2) {
                val sb = StringBuilder()
                for (i in 1 until functionAndData.size) {
                    sb.append(functionAndData[i])
                }
                // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
                return sb.toString()
            }
            return null
        }

        @JvmStatic
        fun getFunctionFromReturnUrl(url: String): String? {
            // temp = _fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
            val temp = url.replace(
                YY_RETURN_DATA,
                EMPTY_STR
            )
            val functionAndData =
                temp.split(SPLIT_MARK.toRegex())
                    .toTypedArray()
            return if (functionAndData.isNotEmpty()) {
                // functionAndData[0] = _fetchQueue
                functionAndData[0]
            } else null
        }

        /**
         * js 文件将注入为第一个script引用
         */
        @JvmStatic
        fun webViewLoadJs(webView: WebView, url: String) {
            var js = "var newscript = document.createElement(\"script\");"
            js += "newscript.src=\"$url\";"
            js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);"
            webView.loadUrl("javascript:$js")
        }

        /**
         * 这里只是加载lib包中assets中的 WebViewJavascriptBridge.js
         */
        @JvmStatic
        fun webViewLoadLocalJs(webView: WebView, path: String) {
            webView.loadUrl("javascript:${BridgeUtil.assetFile2Str(webView.getContext(), path)}")
        }

        @JvmStatic
        fun assetFile2Str(context: Context, urlStr: String): String? {
            var iSteam: InputStream? = null
            try {
                iSteam = context.assets.open(urlStr)
                val bufferedReader =
                    BufferedReader(InputStreamReader(iSteam))
                var line: String? = null
                val sb = StringBuilder()
                do {
                    line = bufferedReader.readLine()

                    if (line != null && !line.matches(Regex.fromLiteral("^\\s*\\/\\/.*"))) { // 去除注释
                        sb.append(line)
                    }
                } while (line != null)
                bufferedReader.close()
                iSteam.close()
                return sb.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (iSteam != null) {
                    try {
                        iSteam.close()
                    } catch (e: IOException) {
                    }
                }
            }
            return null
        }

        @JvmStatic
        fun <T> getT(any: Any, index: Int): T? {
            try {
                val genericSuperclass: Type = any.javaClass.genericSuperclass
                if (genericSuperclass is ParameterizedType) {
                    val actualTypeArguments =
                        genericSuperclass.actualTypeArguments
                    if (actualTypeArguments.size > index) {
                        return (actualTypeArguments[index] as Class<T>).newInstance()
                    }
                } else {
                    return null
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }
    }


}