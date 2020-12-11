package com.cwand.lib.ktx

/**
 * @author : chunwei
 * @date : 2020/12/9
 * @description :
 *
 */
abstract class BaseRepository {

    /**
     * 是否使用缓存,默认不使用缓存
     */
    protected open fun useCache(): Boolean {
        return false
    }

}