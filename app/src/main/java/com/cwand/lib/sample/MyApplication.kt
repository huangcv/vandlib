package com.cwand.lib.sample

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import com.cwand.lib.ktx.AndLib
import com.cwand.lib.ktx.exception.AppException
import com.cwand.lib.ktx.exception.ExceptionEngine
import com.cwand.lib.ktx.extensions.logD
import com.cwand.lib.ktx.widgets.*

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
        val config = NiceLoadingConfig
            .obtain()
//            .baseContentIdRes(R.id.and_lib_base_content_root)
            .defaultState(State.ERROR)
            .emptyDrawable(R.drawable.ic_empty)
            .noNetworkDrawable(R.drawable.ic_no_network_def)
            .errorDrawable(R.drawable.ic_error)
            .noNetworkClickIdRes(R.id.tv_no_network)
            .viewProvider(MyNiceLoadingAdapter())
        NiceLoading.config(config)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        "Application 配置变化".logD()
    }

    inner class AppExceptionHandler : ExceptionEngine.ExceptionHandler {
        override fun handleException(exception: Throwable): Boolean {
            if (exception is AppException) {
                "${exception.code}, ${exception.error}".logD()
            } else {
                exception.message?.logD()
            }
            return false
        }
    }

    class MyNiceLoadingAdapter : ViewProvider() {

        override fun provideView(context: Context, state: State): View? {
            return when (state) {
                State.LOADING -> LayoutInflater.from(context)
                    .inflate(R.layout.loading, null)
                State.EMPTY -> LayoutInflater.from(context).inflate(R.layout.empty, null)
                State.NO_NETWORK -> LayoutInflater.from(context)
                    .inflate(R.layout.no_network, null)
                State.ERROR -> LayoutInflater.from(context).inflate(R.layout.error, null)
                else -> null
            }
        }
    }
}