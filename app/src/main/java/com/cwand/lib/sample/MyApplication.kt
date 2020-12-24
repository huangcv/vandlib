package com.cwand.lib.sample

import android.app.Application
import android.content.res.Configuration
import com.cwand.lib.ktx.AndLib
import com.cwand.lib.ktx.exception.AppException
import com.cwand.lib.ktx.exception.ExceptionEngine
import com.cwand.lib.ktx.ext.logD

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description :
 *
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndLib.init(this)
        ExceptionEngine.registerExceptionHandler(AppExceptionHandler())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        "Application 配置变化".logD()
    }

    inner class AppExceptionHandler: ExceptionEngine.ExceptionHandler{
        override fun handleException(exception: Throwable): Boolean {
            if (exception is AppException) {
                "${exception.code}, ${exception.error}".logD()
            } else {
                exception.message?.logD()
            }
            return false
        }
    }
}