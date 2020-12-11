package com.cwand.lib.ktx.interceptors.log

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * @author : chunwei
 * @date : 2020/12/11
 * @description :日志拦截器
 *
 */
class LogInterceptor private constructor(private val builder: Builder) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (builder.level == LogLevel.NONE) {
            return chain.proceed(request)
        }
        //打印请求日志
        LogPrinter.printRequest(builder, request, chain)
        try {
            val startNs = System.nanoTime()
            //发起网络请求
            val response = processResponse(chain, request)
            //接收到响应的时间
            val receivedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            LogPrinter.printResponse(builder, request, response, receivedMs)
            return response
        } catch (e: Exception) {
            builder.logger.log("the HTTP request is failed: $e ${LogPrinter.LINE_SEPARATOR}")
            throw  e
        }
    }

    private fun processResponse(chain: Interceptor.Chain, request: Request): Response {
        return chain.proceed(request)
    }

    class Builder {
        var level = LogLevel.ALL
        var logger = ILogger.DEFAULT
    }
}