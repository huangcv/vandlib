package com.cwand.lib.ktx.exception

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 错误信息枚举
 *
 */
enum class Error(val code: Int, val error: String) {
    /**
     * 未知错误
     */
    UNKNOWN(1000, "请求失败，请稍后再试"),

    /**
     * 解析错误
     */
    PARSE_ERROR(1001, "解析错误，请稍后再试"),

    /**
     * 网络错误
     */
    NETWORK_ERROR(1002, "网络连接错误，请稍后重试"),

    /**
     * 证书出错
     */
    SSL_ERROR(1004, "证书出错，请稍后再试"),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(1006, "网络连接超时，请稍后重试"),

    /**
     * 授权失败(登录信息失效)
     */
    AUTHORIZATION_FAILED(1007, "登录信息失效"),

    /**
     * 其他异常
     */
    OTHER(1314520, "");

}