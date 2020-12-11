package com.cwand.lib.ktx.interceptors.log

import okhttp3.internal.platform.Platform

/**
 * @author : chunwei
 * @date : 2020/12/11
 * @description :
 *
 */
interface ILogger {
    fun log(msg: String?, level: Int = Platform.INFO)

    companion object {
        val DEFAULT = object : ILogger {
            override fun log(msg: String?, level: Int) {
                Platform.get().log("$msg", level, null)
            }
        }
    }
}