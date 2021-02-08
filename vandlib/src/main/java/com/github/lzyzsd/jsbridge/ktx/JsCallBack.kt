package com.github.lzyzsd.jsbridge.ktx

/**
 * @author : chunwei
 * @date : 2021/1/29
 * @description :
 *
 */
abstract class JsCallBack<T> {

    abstract fun handler(methodName: String, data: T, function: CallBackFunction)

}