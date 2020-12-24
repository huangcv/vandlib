package com.cwand.lib.ktx.entity

/**
 * @author : chunwei
 * @date : 2020/12/22
 * @description :
 *
 */
interface BaseResponse<T> {
    fun isSuccess(): Boolean
    fun responseCode(): Int
    fun responseMsg(): String
    fun responseData(): T
}