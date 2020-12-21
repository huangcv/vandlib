package com.cwand.lib.ktx

/**
 * @author : chunwei
 * @date : 2020/12/16
 * @description : 数据缓存接口
 *
 */
interface ICacheData {

    fun isCacheData(cacheKey: String): Boolean

    fun doCacheData(cacheKey: String, cacheValue: String)

}