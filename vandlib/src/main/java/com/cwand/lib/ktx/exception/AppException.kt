package com.cwand.lib.ktx.exception

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 自定义App错误信息
 *
 */
class AppException : Exception {

    val code: Int
    val error: String
    val errorLog: String

    constructor(code: Int, error: String = "", errorLog: String = "") : super(error) {
        this.code = code
        this.error = error
        this.errorLog = errorLog
    }

    constructor(error: Error, throwable: Throwable? = null):super(error.error) {
        this.code = error.code
        this.error = error.error
        this.errorLog = throwable?.message ?: ""
    }

}