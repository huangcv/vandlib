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

        fun registerExceptionHandler(handler: ExceptionHandler) {
            instance.exceptionHandler = handler
        }

        @MainThread
        fun handleException(exception: Throwable): AppException? {
            return instance.exceptionHandler?.handleException(exception)
        }

    }

    private object Holder {
        val holder = ExceptionEngine()
    }

    interface ExceptionHandler {
        fun handleException(exception: Throwable): AppException?
    }

}