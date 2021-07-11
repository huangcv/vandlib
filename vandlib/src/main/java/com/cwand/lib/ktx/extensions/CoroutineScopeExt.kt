package com.cwand.lib.ktx.extensions

import android.util.Log
import com.cwand.lib.ktx.exception.AppException
import com.cwand.lib.ktx.exception.Error
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
    private val launcherCallback: LauncherCallback,
    //协程域
    private val coroutineScope: CoroutineScope,
    //需要执行的代码块
    private val block: suspend CoroutineScope.() -> Unit,
) {

    private var exceptionFun: (AppException) -> Unit = {}
    private var completedFun: () -> Unit = {}
    private var successFun: () -> Unit = {}
    private var startFun: () -> Unit = {}

    private val defaultLauncherCallback: LauncherCallback = object : LauncherCallback {
        override fun onStart() {
            launcherCallback.onStart()
            startFun()
        }

        override fun onException(exception: AppException) {
            launcherCallback.onException(exception)
            exceptionFun(exception)
        }

        override fun onSuccess() {
            launcherCallback.onSuccess()
            successFun()
        }

        override fun onCompleted() {
            launcherCallback.onCompleted()
            completedFun()
        }

    }

    fun onStart(sf: () -> Unit = {}) = apply {
        startFun = sf
    }

    /**
     * 出现异常执行
     */
    fun onException(ef: (AppException) -> Unit = {}) = apply {
        exceptionFun = ef
    }

    /**
     * block 正常执行完之后回调
     */
    fun onSuccess(sf: () -> Unit = {}) = apply {
        successFun = sf
    }

    /**
     *  一定会执行
     */
    fun onCompleted(ff: () -> Unit = {}) = apply {
        completedFun = ff
    }

    /**
     * 启动协程
     */
    fun start(): Job {
        return coroutineScope.safeLauncher(
            defaultLauncherCallback,
            block
        )
    }
}

interface LauncherCallback {

    fun onStart()

    /**
     * 出现异常执行
     */
    fun onException(exception: AppException)

    /**
     * block 正常执行完之后回调
     */
    fun onSuccess()

    /**
     *  一定会执行
     */
    fun onCompleted()
}

/**
 * 统一处理回调
 */
fun CoroutineScope.safeLauncher(
    launcherCallback: LauncherCallback,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("VandLib", throwable.message, throwable)
        if (!ExceptionEngine.handleException(throwable)) {
            if (throwable is AppException) {
                launcherCallback.onException(throwable)
            } else {
                launcherCallback.onException(
                    ExceptionEngine.getAppException(throwable)
                )
            }
        }
    }

    return launch(coroutineExceptionHandler) {
        try {
            withContext(Dispatchers.Main) {
                launcherCallback.onStart()
            }
            block(this)
        } finally {
            launcherCallback.onCompleted()
        }
    }
}

fun CoroutineScope.launcher(
    launcherCallback: LauncherCallback,
    block: suspend CoroutineScope.() -> Unit,
): Launcher {
    return Launcher(launcherCallback, this, block)
}