package com.cwand.lib.ktx

import com.cwand.lib.ktx.entity.BaseResp
import com.cwand.lib.ktx.exception.AppException
import com.cwand.lib.ktx.interceptors.CacheInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author : chunwei
 * @date : 2020/12/9
 * @description :
 *
 */
abstract class BaseRepository : IDataRepository, BaseNetworkApi, ICacheData {

    override suspend fun <T> request(block: suspend () -> BaseResp<T?>): BaseResp<T?> {
        return withContext(Dispatchers.IO) {
            block().apply {
                //网络请求结果解析,统一处理,比如:登录失效等错误码
                when (code) {
                    //数据有效
                    200 -> {
                        //是否需要缓存
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            //切到主线程进行处理
                            //通过抛出异常来中断执行流
                            throw AppException(code, msg)
                        }
                    }
                }
            }
        }
    }

    override suspend fun <T, R> T.parse(block: suspend (T) -> R?): R? {
        return withContext(Dispatchers.IO) {
            block(this@parse)
        }
    }

    override fun <T> getApi(apiClass: Class<T>, baseUrl: String): T {
        return getRetrofit().create(apiClass)
    }

    override fun getOkHttpClient(): OkHttpClient {
        return OkHttpClientManager.getOkClient()
    }

    override fun getRetrofit(): Retrofit {
        return RetrofitManager.getRetrofit()
    }

    override fun isCacheData(cacheKey: String): Boolean {
        return false
    }

    override fun doCacheData(cacheValue: String) {
    }
}