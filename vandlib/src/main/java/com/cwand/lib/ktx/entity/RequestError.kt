package com.cwand.lib.ktx.entity

/**
 * 错误类型
 */
enum class RequestError(val code: Int, val errorMsg: String) {

    UNKNOWN(10000, "请求失败,请稍后重试"),
    PARSE_ERROR(20000, "解析错误,请稍后重试"),
    NETWORK_ERROR(30000, "网络连接错误,请稍后重试"),
    SSL_ERROR(40000, "证书错误,请稍后重试"),
    TIMEOUT_ERROR(50000, "请求超时,请稍后重试")

}