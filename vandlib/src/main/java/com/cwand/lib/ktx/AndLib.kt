package com.cwand.lib.ktx

import android.app.Application
import android.content.Context

/**
 * @author : chunwei
 * @date : 2020/12/16
 * @description : 基类库入口
 *
 */
class AndLib private constructor() {

    private var applicationContext: Context? = null

    companion object {
        private val instance = Holder.holder

        @JvmStatic
        fun init(applicationContext: Application) {
            instance.applicationContext = applicationContext.applicationContext
        }

        fun getAppContext():Context {
            return instance.applicationContext ?: throw IllegalStateException("Andlib must be initialized with call method init()")
        }
    }

    private object Holder {
        val holder = AndLib()
    }

}