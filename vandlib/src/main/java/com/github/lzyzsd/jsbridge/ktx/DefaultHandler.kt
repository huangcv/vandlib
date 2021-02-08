package com.github.lzyzsd.jsbridge.ktx

/**
 * @author : chunwei
 * @date : 2021/1/29
 * @description :
 *
 */
class DefaultHandler : BridgeHandler {
    override fun handler(data: String?, function: CallBackFunction) {
        function.onCallBack("DefaultHandler response data")
    }
}