package com.cwand.lib.ktx

import com.cwand.lib.ktx.entity.BaseResp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BaseNetworkRepository : IDataRepository {
    override suspend fun <T> request(block: suspend () -> BaseResp<T?>): BaseResp<T?> {
        return withContext(Dispatchers.IO) {
            block().apply {
                //网络请求结果解析,统一处理,比如:登录失效,鉴权失败等错误码
                when (responseCode()) {
                    200 -> {
                        //请求成功
                    }
                    //重新登录
                    10000 -> {
                        withContext(Dispatchers.Main) {
                            //切到主线程进行处理
                            //通过抛出一个空白异常来中断执行流
                            //TODO 抛出空白异常
                        }
                    }
                    else -> {
                        //TODO 抛出异常
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
}