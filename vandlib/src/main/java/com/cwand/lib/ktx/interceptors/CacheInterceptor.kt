package com.cwand.lib.ktx.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author : chunwei
 * @date : 2020/12/9
 * @description : 缓存拦截器
 *
 */
class CacheInterceptor private constructor() : Interceptor {

    private var realCacheInterceptor: Interceptor? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        return realCacheInterceptor?.intercept(chain) ?: chain.proceed(chain.request())
    }

    companion object {
        private val instance = Holder.holder

        @JvmStatic
        fun bindRealInterceptor(interceptor: Interceptor) {
            instance.realCacheInterceptor = interceptor
        }

        @JvmStatic
        fun getInterceptor(): Interceptor {
            return instance
        }
    }

    private object Holder {
        val holder = CacheInterceptor()
    }
}