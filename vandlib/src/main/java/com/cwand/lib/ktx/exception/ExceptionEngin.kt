package com.cwand.lib.ktx.exception

import androidx.annotation.MainThread
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLProtocolException

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 异常处理引擎
 *
 */
class ExceptionEngine private constructor() {

    private var exceptionHandler: ExceptionHandler? = null

    companion object {
        private val instance = Holder.holder

        @JvmStatic
        fun registerExceptionHandler(handler: ExceptionHandler) {
            instance.exceptionHandler = handler
        }

        @JvmStatic
        @MainThread
        fun handleException(exception: Throwable): Boolean {
            return instance.exceptionHandler?.handleException(exception) ?: false
        }

        fun getAppException(throwable: Throwable): AppException {
            if (throwable is SocketTimeoutException) {
                //连接超时
                return AppException(Error.TIMEOUT_ERROR.code, Error.TIMEOUT_ERROR.error)
            } else if (throwable is SSLProtocolException || throwable is SSLException || throwable is SSLHandshakeException) {
                //SSL证书错误
                return AppException(Error.SSL_ERROR.code, Error.SSL_ERROR.error)
            } else if (throwable is JsonParseException || throwable is JsonSyntaxException || throwable is JSONException || throwable is JsonIOException) {
                //解析异常
                return AppException(Error.PARSE_ERROR.code, Error.PARSE_ERROR.error)
            } else if (throwable is UnknownHostException || throwable is UnknownServiceException) {
                //网络错误:未知主机地址等
                return AppException(Error.NETWORK_ERROR.code, Error.NETWORK_ERROR.error)
            } else {
                //未知错误
                return AppException(Error.UNKNOWN.code, Error.UNKNOWN.error)
            }
        }

    }

    private object Holder {
        val holder = ExceptionEngine()
    }

    interface ExceptionHandler {
        //对错误进行统一处理
        fun handleException(exception: Throwable): Boolean
    }

}