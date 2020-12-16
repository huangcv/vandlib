package com.cwand.lib.ktx.ext

import com.cwand.lib.ktx.exception.ExceptionEngine
import kotlinx.coroutines.*

/**
 * @author : chunwei
 * @date : 2020/12/8
 * @description : 协程扩展封装
 *
 */

/**
 * 封装统一的发射器,结果和异常可以做统一处理
 */
open class Launcher(
    //协程域
    private val coroutineScope: CoroutineScope,
    //需要执行的代码块
    private val block: suspend CoroutineScope.() -> Unit,
) {

    private var exceptionFun: (Throwable) -> Unit = {}
    private var finallyFun: () -> Unit = {}

    /**
     * 出现异常执行
     */
    fun onException(ef: (Throwable) -> Unit = {}): Launcher {
        exceptionFun = ef
        return this
    }

    /**
     *  一定会执行
     */
    fun onFinally(ff: () -> Unit = {}): Launcher {
        finallyFun = ff
        return this
    }

    /**
     * 启动协程
     */
    fun start(): Job {
        return coroutineScope.safeLauncher(block, finallyFun, exceptionFun)
    }
}

/**
 * 统一处理异常
 */
fun CoroutineScope.safeLauncher(
    block: suspend CoroutineScope.() -> Unit,
    onFinally: () -> Unit = {},
    onException: (Throwable) -> Unit = {},
): Job {
    return launch(CoroutineExceptionHandler { _, throwable ->
        onException(ExceptionEngine.handleException(throwable) ?: throwable)
    }) {
        try {
            block(this)
        } finally {
            onFinally()
        }
    }
}

fun CoroutineScope.launcher(block: suspend CoroutineScope.() -> Unit): Launcher {
    return Launcher(this, block)
}