package com.cwand.lib.ktx.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.cwand.lib.ktx.AndLib

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

    @JvmStatic
    @MainThread
    fun toastLong(context: Context, message: CharSequence) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    @MainThread
    fun toastLong(context: Context, @StringRes messageRes: Int) {
        Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun safeToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            //子线程
            Looper.prepare()
            Toast.makeText(AndLib.getAppContext(), message, duration).show()
            Looper.loop()
        } else {
            //主线程
            Toast.makeText(AndLib.getAppContext(), message, duration).show()
        }
    }

    @JvmStatic
    fun safeToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            //子线程
            Looper.prepare()
            Toast.makeText(AndLib.getAppContext(), messageRes, duration).show()
            Looper.loop()
        } else {
            //主线程
            Toast.makeText(AndLib.getAppContext(), messageRes, duration).show()
        }
    }
}