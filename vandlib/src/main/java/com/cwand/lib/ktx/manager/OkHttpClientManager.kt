package com.cwand.lib.ktx.manager

import com.cwand.lib.ktx.utils.CreatorFactory
import com.cwand.lib.ktx.interceptors.CacheInterceptor
import com.cwand.lib.ktx.interceptors.log.LogInterceptor
import com.cwand.lib.ktx.repository.RepositoryConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : OkHttp客户端管理工具
 *
 */
class OkHttpClientManager private constructor() {

    private var okHttpClient: OkHttpClient? = null
    private val okHttpClientBuilder: OkHttpClient.Builder by lazy { defaultOkHttpClientBuilder() }

    private
    val okHttpClientFactory: CreatorFactory<OkHttpClient> = DefaultOkHttpClientFactory()
    private var customOkHttpClientFactory: CreatorFactory<OkHttpClient>? = null

    private object Holder {
        val holder = OkHttpClientManager()
    }

    private fun defaultOkHttpClientBuilder(): OkHttpClient.Builder {
        val retryConnectEnable = RepositoryConfig.networkRepositoryConfig.retryConnectCount > 0
        return OkHttpClient.Builder()
            .readTimeout(RepositoryConfig.networkRepositoryConfig.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(RepositoryConfig.networkRepositoryConfig.writeTimeout,
                TimeUnit.SECONDS)
            .connectTimeout(RepositoryConfig.networkRepositoryConfig.connectTimeout,
                TimeUnit.SECONDS)
            .addInterceptor(LogInterceptor.Builder().build())
            .addInterceptor(CacheInterceptor.getInterceptor())
            .retryOnConnectionFailure(retryConnectEnable)
    }

    companion object {
        private val instance: OkHttpClientManager = Holder.holder

        fun getOkClientCreatorFactory(): CreatorFactory<OkHttpClient> {
            return instance.customOkHttpClientFactory ?: instance.okHttpClientFactory
        }

        fun getOkClient(): OkHttpClient {
            if (instance.okHttpClient == null) {
                instance.okHttpClient = createNew()
            }
            return instance.okHttpClient!!
        }

        fun getOkClientBuilder(): OkHttpClient.Builder {
            return instance.okHttpClientBuilder
        }

        fun newOkHttpClient(
            factory: CreatorFactory<OkHttpClient> = instance.customOkHttpClientFactory
                ?: instance.okHttpClientFactory,
        ): OkHttpClient {
            return factory.create()
        }

        private fun createNew(): OkHttpClient {
            return instance.customOkHttpClientFactory?.create()
                ?: instance.okHttpClientFactory.create()
        }
    }

    internal class DefaultOkHttpClientFactory : CreatorFactory<OkHttpClient> {
        override fun create(): OkHttpClient {
            return create(Any())
        }

        override fun create(data: Any): OkHttpClient {
            return instance.defaultOkHttpClientBuilder()
                .build()
        }
    }

}