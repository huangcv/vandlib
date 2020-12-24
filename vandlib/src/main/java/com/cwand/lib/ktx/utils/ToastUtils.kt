package com.cwand.lib.ktx.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes

/**
 * @author : chunwei
 * @date : 2020/12/14
 * @description : Toast工具,没有处理子线程
 *
 */
object ToastUtils {

    @JvmStatic
    @MainThread
    fun toast(context: Context, message: CharSequence) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    @MainThread
    fun toast(context: Context, @StringRes messageRes: Int) {
        Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }
}