package com.github.lzyzsd.jsbridge.ktx

/**
 * @author : chunwei
 * @date : 2021/1/29
 * @description :
 *
 */
interface WebViewJavascriptBridge {

    fun send(data: String)
    fun send(data: String, function: CallBackFunction?)
}