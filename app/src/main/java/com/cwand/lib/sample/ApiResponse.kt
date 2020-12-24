package com.cwand.lib.sample

import com.cwand.lib.ktx.entity.BaseResponse

/**
 * @author : chunwei
 * @date : 2020/12/22
 * @description :
 *
 */
data class ApiResponse<T>(val code: Int, val msg: String, val result: T) : BaseResponse<T> {
    override fun isSuccess(): Boolean = code == 200

    override fun responseCode(): Int = code

    override fun responseMsg(): String = msg

    override fun responseData(): T = result
}