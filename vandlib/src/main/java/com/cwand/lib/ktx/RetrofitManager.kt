package com.cwand.lib.ktx

import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description :Retrofit 管理类
 *
 */
class RetrofitManager private constructor() {

    private val defaultRetrofitFactory: CreatorFactory<Retrofit> by lazy {
        DefaultRetrofitFactory()
    }

    private var customRetrofitFactory: CreatorFactory<Retrofit>? = null

    private var retrofit: Retrofit? = null

    private var retrofitBuilder: Retrofit.Builder? = null

    private object Holder {
        val holder = RetrofitManager()
    }

    companion object {
        private val instance = Holder.holder

        fun getRetrofit(): Retrofit {
            if (instance.retrofit == null) {
                instance.retrofit = instance.customRetrofitFactory?.create()
                    ?: instance.defaultRetrofitFactory.create()
            }
            return instance.retrofit!!
        }

        private fun defaultRetrofitBuilder(
            baseUrl: String,
            callAdapterFactory: CallAdapter.Factory? = null,
            converterFactory: Converter.Factory,
        ): Retrofit.Builder {
            val builder = Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .client(OkHttpClientManager.getOkClient())
            if (callAdapterFactory != null) {
                builder.addCallAdapterFactory(callAdapterFactory)
            }
            return builder
        }

        fun getRetrofitBuilder(): Retrofit.Builder {
            if (instance.retrofitBuilder == null) {
                instance.retrofitBuilder =
                    defaultRetrofitBuilder(RepositoryConfig.networkRepositoryConfig.baseUrl, null,
                        GsonConverterFactory.create())
            }
            return instance.retrofitBuilder!!
        }

        fun getNewRetrofit(): Retrofit {
            return getNewRetrofit(RepositoryConfig.networkRepositoryConfig.baseUrl)
        }

        fun getNewRetrofit(
            baseUrl: String,
            callAdapterFactory: CallAdapter.Factory? = null,
            converterFactory: Converter.Factory = GsonConverterFactory.create(),
        ): Retrofit {
            return defaultRetrofitBuilder(baseUrl, callAdapterFactory, converterFactory).build()
        }

        fun <T> createService(apiClass: Class<T>): T {
            return getRetrofit().create(apiClass)
        }

        fun <T> createService(apiClass: Class<T>, baseUrl: String): T {
            return getNewRetrofit(baseUrl).create(apiClass)
        }
    }

    internal class DefaultRetrofitFactory : CreatorFactory<Retrofit> {
        override fun create(): Retrofit {
            return getNewRetrofit()
        }
    }

}