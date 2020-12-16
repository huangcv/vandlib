package com.cwand.lib.ktx

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : 实例生成工厂
 *
 */
interface CreatorFactory<T> {
    fun create(): T
}