package com.github.lzyzsd.jsbridge.ktx

/**
 * @author : chunwei
 * @date : 2021/1/28
 * @description :
 *
 */
interface BridgeHandler {
    fun handler(data: String?, function: CallBackFunction)
}