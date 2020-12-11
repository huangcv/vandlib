package com.cwand.lib.ktx.interceptors.log

import okhttp3.*
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.EOFException
import okio.GzipSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author : chunwei
 * @date : 2020/12/11
 * @description : 请求日志打印器
 *
 */
class LogPrinter private constructor() {

    companion object {

        val LINE_SEPARATOR = System.lineSeparator()
        private const val JSON_INDENT = 3
        private val OOM_OMITTED = LINE_SEPARATOR + "Output omitted because of Object size."


        @JvmStatic
        fun printRequest(
            builder: LogInterceptor.Builder,
            request: Request,
            chain: Interceptor.Chain,
        ) {
            //请求地址
            val requestUrl = request.url.toUrl().toString()
            //请求方式
            val requestMethod = request.method
            val protocol = (chain.connection()?.protocol() ?: Protocol.HTTP_1_1).toString()
            val urlStr = " Request --->  $requestUrl  $protocol $requestMethod $LINE_SEPARATOR"
            //打印请求地址
            builder.logger.log(urlStr)
            //请求头
            val requestHeaders = request.headers
            val headerStr = getHeaderStr(requestHeaders)
            //打印请求头
            builder.logger.log(headerStr)
            //请求体
            val requestBody = request.body
            val bodyStr = requestBody?.let {
                "Body: ${getBodyStr(it, requestHeaders)}"
            } ?: "Body: $LINE_SEPARATOR"
            //打印请求体
            builder.logger.log(bodyStr)
        }

        /**
         * 打印返回的响应
         */
        @JvmStatic
        fun printResponse(
            builder: LogInterceptor.Builder,
            request: Request,
            response: Response,
            receivedMs: Long,
        ) {
            //请求地址
            val requestUrl = request.url.toUrl().toString()
            //响应码
            val code = response.code
            val msg = response.message
            val urlStr = " Response <---  $requestUrl （$receivedMs）$LINE_SEPARATOR"
            builder.logger.log(urlStr)
            val codeStr = "$code  $msg $LINE_SEPARATOR"
            builder.logger.log(codeStr)
            builder.logger.log(getResponseStr(response))
            // TODO: 2020/12/11 待完善
        }

        private fun getResponseStr(response: Response): String {
            return if (!response.promisesBody()) {
                "End request - Promises Body"
            } else if (bodyHasUnknownEncoding(response.headers)) {
                "encoded body omitted"
            } else {
                getResponseBodyStr(response.headers, response.body)
            }
        }

        private fun getResponseBodyStr(headers: Headers, body: ResponseBody?): String {
            return body?.let {
                val contentLength = it.contentLength()
                val source = it.source()
                source.request(Long.MAX_VALUE)
                var buffer = source.buffer
                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }
                val contentType = it.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                    ?: StandardCharsets.UTF_8
                if (!buffer.isProbablyUtf8()) {
                    return "End request - binary ${buffer.size}:byte body omitted"
                }
                if (contentLength != 0L) {
                    return getJsonString(buffer.clone().readString(charset))
                }
                return if (gzippedLength != null) {
                    "End request - ${buffer.size}:byte, $gzippedLength-gzipped-byte body"
                } else {
                    "End request - ${buffer.size}:byte body"
                }
            } ?: ""
        }

        private fun getHeaderStr(headers: Headers): String {
            val builder = StringBuilder()
            headers.forEach { pair ->
                builder.append("${pair.first}: ${pair.second}").append(LINE_SEPARATOR)
            }
            return builder.dropLast(1).toString()
        }

        private fun getBodyStr(body: RequestBody, requestHeaders: Headers): String {
            return try {
                when {
                    bodyHasUnknownEncoding(requestHeaders) -> {
                        "the encoded body omitted $LINE_SEPARATOR"
                    }
                    body.isDuplex() -> {
                        "duplex request body omitted $LINE_SEPARATOR"
                    }
                    body.isOneShot() -> {
                        "one-shot body omitted $LINE_SEPARATOR"
                    }
                    else -> {
                        val buffer = Buffer()
                        body.writeTo(buffer)
                        val contentType = body.contentType()
                        val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                            ?: StandardCharsets.UTF_8
                        if (buffer.isProbablyUtf8()) {
                            getJsonString(buffer.readString(charset)) + LINE_SEPARATOR + "${body.contentLength()}-byte body"
                        } else {
                            "binary ${body.contentLength()}-byte body omitted $LINE_SEPARATOR"
                        }
                    }
                }
            } catch (e: Exception) {
                "error: ${e.message} $LINE_SEPARATOR"
            }
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers["Content-Encoding"] ?: return false
            return !contentEncoding.equals("identity", ignoreCase = true) &&
                    !contentEncoding.equals("gzip", ignoreCase = true)
        }

        private fun getJsonString(msg: String): String {
            val message: String
            message = try {
                when {
                    msg.startsWith("{") -> {
                        val jsonObject = JSONObject(msg)
                        jsonObject.toString(JSON_INDENT)
                    }
                    msg.startsWith("[") -> {
                        val jsonArray = JSONArray(msg)
                        jsonArray.toString(JSON_INDENT)
                    }
                    else -> {
                        msg
                    }
                }
            } catch (e: JSONException) {
                msg
            } catch (e1: OutOfMemoryError) {
                OOM_OMITTED
            }
            return message
        }

    }

}

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}