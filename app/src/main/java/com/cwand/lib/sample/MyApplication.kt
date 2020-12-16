package com.cwand.lib.sample

import android.app.Application
import android.content.res.Configuration
import com.cwand.lib.ktx.AndLib
import com.cwand.lib.ktx.ext.logD

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description :
 *
 */
class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        AndLib.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        "Application 配置变化".logD()
    }
}