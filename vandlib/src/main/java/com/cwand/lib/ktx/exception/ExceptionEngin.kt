package com.cwand.lib.ktx.exception

import androidx.annotation.MainThread

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 异常处理引擎
 *
 */
class ExceptionEngine private constructor() {

    private var exceptionHandler: ExceptionHandler? = null

    companion object {
        private val instance = Holder.holder

        @JvmStatic
        fun registerExceptionHandler(handler: ExceptionHandler) {
            instance.exceptionHandler = handler
        }

        @JvmStatic
        @MainThread
        fun handleException(exception: Throwable): Boolean {
            return instance.exceptionHandler?.handleException(exception) ?: false
        }

    }

    private object Holder {
        val holder = ExceptionEngine()
    }

    interface ExceptionHandler {
        //对错误进行统一处理
        fun handleException(exception: Throwable): Boolean
    }

}