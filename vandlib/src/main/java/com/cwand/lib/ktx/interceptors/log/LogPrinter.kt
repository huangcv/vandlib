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
        private const val maxPrettyJsonLength = 10000

        val LINE_SEPARATOR = System.lineSeparator()
        private const val JSON_INDENT = 3
        private val OOM_OMITTED = LINE_SEPARATOR + "Output omitted because of Object size."


        @JvmStatic
        fun printRequest(
            builder: LogInterceptor.Builder,
            request: Request,
            chain: Interceptor.Chain,
        ) {
            //为了不让日志被隔开,这里是将所有信息放到一起,一次打印出来
            val requestLogContainer  =  StringBuilder()
            //请求地址
            val requestUrl = request.url.toUrl().toString()
            //请求方式
            val requestMethod = request.method
            val protocol = (chain.connection()?.protocol() ?: Protocol.HTTP_1_1).toString()
            val urlStr = "Request ---> $requestUrl $protocol $requestMethod $LINE_SEPARATOR"
            requestLogContainer.append(urlStr)
            //请求头
            requestLogContainer.append("Headers:$LINE_SEPARATOR")
            val requestHeaders = request.headers
            val headerStr = getHeaderStr(requestHeaders)
            requestLogContainer.append(headerStr)
            requestLogContainer.append(LINE_SEPARATOR)
            //请求体
            val requestBody = request.body
            val bodyStr = requestBody?.let {
                "Body:${getBodyStr(it, requestHeaders)}"
            } ?: "Body: $LINE_SEPARATOR  null $LINE_SEPARATOR"
            //打印请求体
            requestLogContainer.append(bodyStr)
            builder.logger.log(requestLogContainer.toString())
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
            //为了不让日志被隔开,这里是将所有信息放到一起,一次打印出来
            val responseLogContainer = StringBuilder()
            //请求地址
            val requestUrl = request.url.toUrl().toString()
            //响应码
            val code = response.code
            val msg = response.message
            val urlStr = "Response <--- $requestUrl（${receivedMs}ms）$LINE_SEPARATOR"
            responseLogContainer.append(urlStr)
//            builder.logger.log(urlStr)
            val codeStr = "Status: $LINE_SEPARATOR  $code  $msg $LINE_SEPARATOR"
            responseLogContainer.append(codeStr)
            //响应头
            val headerStr = getHeaderStr(response.headers)
            responseLogContainer.append("Headers: $LINE_SEPARATOR$headerStr")
            builder.logger.log(responseLogContainer.toString())
            builder.logger.log("Body: $LINE_SEPARATOR")
            printResponseLog(builder, getResponseStr(response))
        }

        fun printErrorRequest(builder: LogInterceptor.Builder, request: Request, errorMsg:String){
            builder.logger.log("Request failed <---> ${request.url.toUrl().toString()} ${request.method} ${LINE_SEPARATOR}Error:$LINE_SEPARATOR  $errorMsg ${LINE_SEPARATOR}End request")
        }

        private fun printResponseLog(builder: LogInterceptor.Builder, responseStr: String) {
            var maxStrLength = 4000
            if (!canPrintPrettyLog(responseStr)){
                //这为了能够优雅的把超长的日志打印出来,综合把单次打印最大长度定在了1000
                maxStrLength = 1000
            }
            var tempStr = responseStr
            while (tempStr.length > maxStrLength) {
                builder.logger.log(tempStr.substring(0, maxStrLength))
                tempStr = tempStr.substring(maxStrLength)
            }
            //剩余部分
            builder.logger.log(tempStr)
        }

        private fun canPrintPrettyLog(msg: String): Boolean {
            return msg.length <= maxPrettyJsonLength
        }

        private fun getResponseStr(response: Response): String {
            return if (!response.promisesBody()) {
                "  Promises Body${LINE_SEPARATOR}End request"
            } else if (bodyHasUnknownEncoding(response.headers)) {
                "  Encoded body omitted${LINE_SEPARATOR}End request"
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
                    return "  binary ${buffer.size} bytes body omitted${LINE_SEPARATOR}End request"
                }
                if (contentLength != 0L) {
                    return getJsonString(buffer.clone().readString(charset)).plus("${LINE_SEPARATOR}End request")
                }
                return if (gzippedLength != null) {
                    "  ${buffer.size} bytes, $gzippedLength-gzipped-byte body${LINE_SEPARATOR}End request"
                } else {
                    "  ${buffer.size} bytes body${LINE_SEPARATOR}End request"
                }
            } ?: ""
        }

        private fun getHeaderStr(headers: Headers): String {
            val builder = StringBuilder()
            headers.forEach { pair ->
                builder.append("  ${pair.first}: ${pair.second}").append(LINE_SEPARATOR)
            }
            return builder.dropLast(1).toString()
        }

        private fun getBodyStr(body: RequestBody, requestHeaders: Headers): String {
            return try {
                when {
                    bodyHasUnknownEncoding(requestHeaders) -> {
                        "$LINE_SEPARATOR  the encoded body omitted $LINE_SEPARATOR"
                    }
                    body.isDuplex() -> {
                        "$LINE_SEPARATOR  duplex request body omitted $LINE_SEPARATOR"
                    }
                    body.isOneShot() -> {
                        "$LINE_SEPARATOR  one-shot body omitted $LINE_SEPARATOR"
                    }
                    else -> {
                        val buffer = Buffer()
                        body.writeTo(buffer)
                        val contentType = body.contentType()
                        val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                            ?: StandardCharsets.UTF_8
                        if (buffer.isProbablyUtf8()) {
                            "(${body.contentLength()}-bytes) $LINE_SEPARATOR  ${getJsonString(buffer.readString(charset)) + LINE_SEPARATOR}"
                        } else {
                            "$LINE_SEPARATOR  binary ${body.contentLength()}-byte body omitted $LINE_SEPARATOR"
                        }
                    }
                }
            } catch (e: Exception) {
                "$LINE_SEPARATOR  error: ${e.message} $LINE_SEPARATOR"
            }
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers["Content-Encoding"] ?: return false
            return !contentEncoding.equals("identity", ignoreCase = true) &&
                    !contentEncoding.equals("gzip", ignoreCase = true)
        }

        private fun getJsonString(msg: String): String {
            //如果超过10000个字符,这里直接返回
            if (!canPrintPrettyLog(msg)) {
                return msg
            }
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