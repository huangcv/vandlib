package com.cwand.lib.ktx.utils

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description :网络状态枚举
 *
 */
enum class NetworkState(val code: Int, val state: String) {
    NETWORK_2G(100, "2G"),
    NETWORK_3G(200, "3G"),
    NETWORK_4G(300, "4G"),
    WIFI(400, "wifi"),
    NETWORK_UNKNOWN(500, "unknown"),
    NETWORK_NO(600, "none");
}