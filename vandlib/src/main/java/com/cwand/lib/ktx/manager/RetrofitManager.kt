package com.cwand.lib.ktx.manager

import android.text.TextUtils
import com.cwand.lib.ktx.utils.CreatorFactory
import com.cwand.lib.ktx.repository.RepositoryConfig
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description :Retrofit 管理类
 *
 */
class RetrofitManager private constructor() {

    private val retrofitInstanceCache: HashMap<String, WeakReference<Retrofit>> by lazy {
        HashMap()
    }

    private val defaultRetrofitFactory: CreatorFactory<Retrofit> by lazy {
        DefaultRetrofitFactory()
    }

    private var customRetrofitFactory: CreatorFactory<Retrofit>? = null

    private var retrofitBuilder: Retrofit.Builder? = null

    private object Holder {
        val holder = RetrofitManager()
    }

    companion object {
        private val instance = Holder.holder

        fun getRetrofit(baseUrl: String): Retrofit {
            var url = baseUrl
            if (TextUtils.isEmpty(url)) {
                url = RepositoryConfig.networkRepositoryConfig.baseUrl
            }
            var retrofit: Retrofit? = null
            if (instance.retrofitInstanceCache.containsKey(url)) {
                retrofit = instance.retrofitInstanceCache[url]?.get()
            }
            if (retrofit == null) {
                retrofit = instance.customRetrofitFactory?.create(url)
                    ?: instance.defaultRetrofitFactory.create(url)
                instance.retrofitInstanceCache[url] = WeakReference(retrofit)
            }
            return retrofit
        }

        private fun createRetrofitBuilder(
            baseUrl: String,
            callAdapterFactory: CallAdapter.Factory? = null,
            converterFactory: Converter.Factory,
            okHttpClient: OkHttpClient = OkHttpClientManager.getOkClient(),
        ): Retrofit.Builder {
            val builder = Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
            if (callAdapterFactory != null) {
                builder.addCallAdapterFactory(callAdapterFactory)
            }
            return builder
        }

        fun getDefaultRetrofitBuilder(): Retrofit.Builder {
            if (instance.retrofitBuilder == null) {
                instance.retrofitBuilder =
                    createRetrofitBuilder(RepositoryConfig.networkRepositoryConfig.baseUrl, null,
                        GsonConverterFactory.create())
            }
            return instance.retrofitBuilder!!
        }

        fun getNewRetrofit(
            baseUrl: String,
            callAdapterFactory: CallAdapter.Factory? = null,
            converterFactory: Converter.Factory = GsonConverterFactory.create(),
        ): Retrofit {
            return createRetrofitBuilder(baseUrl, callAdapterFactory, converterFactory).build()
        }

        fun <T> createService(apiClass: Class<T>): T {
            return getRetrofit(RepositoryConfig.networkRepositoryConfig.baseUrl).create(apiClass)
        }

        fun <T> createService(apiClass: Class<T>, baseUrl: String): T {
            return getRetrofit(baseUrl).create(apiClass)
        }
    }

    internal class DefaultRetrofitFactory : CreatorFactory<Retrofit> {
        override fun create(): Retrofit {
            return create(RepositoryConfig.networkRepositoryConfig.baseUrl)
        }

        override fun create(data: Any): Retrofit {
            return createRetrofitBuilder(data.toString(), null,
                GsonConverterFactory.create()).build()
        }
    }

}