package com.cwand.lib.ktx.repository

import com.cwand.lib.ktx.manager.OkHttpClientManager
import com.cwand.lib.ktx.manager.RetrofitManager
import com.cwand.lib.ktx.entity.BaseResponse
import com.cwand.lib.ktx.exception.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author : chunwei
 * @date : 2020/12/9
 * @description :
 *
 */
abstract class BaseRepository : IDataRepository, BaseNetworkApi, ICacheData {

    override suspend fun <T> request(block: suspend () -> BaseResponse<T>): T {
        return withContext(Dispatchers.IO) {
            block().let {
                if (it.isSuccess()) {
                    it.responseData()
                } else {
                    withContext(Dispatchers.Main) {
                        throw AppException(it.responseCode(), it.responseMsg())
                    }
                }
            }
        }
    }

    override suspend fun <T, R> T.parse(block: suspend (T) -> R): R {
        return withContext(Dispatchers.IO) {
            block(this@parse)
        }
    }

    override fun <T> getApi(apiClass: Class<T>, baseUrl: String): T {
        return getRetrofit(baseUrl).create(apiClass)
    }

    override fun <T> getApi(apiClass: Class<T>): T {
        return getRetrofit("").create(apiClass)
    }

    override fun getOkHttpClient(): OkHttpClient {
        return OkHttpClientManager.getOkClient()
    }

    override fun getRetrofit(baseUrl: String): Retrofit {
        return RetrofitManager.getRetrofit(baseUrl)
    }

    override fun isCacheData(cacheKey: String): Boolean {
        return false
    }

    override fun doCacheData(cacheKey: String, cacheValue: String) {
    }
}