package com.cwand.lib.ktx.ext

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * 空操作和非空操作
 */
inline fun <reified T> T?.notNull(notNullAction: (T) -> Unit, nullAction: () -> Unit = {}) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction.invoke()
    }
}

fun Any.safeRun(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Any.safeRun(block: () -> Unit, exception: (Exception) -> Unit = {}) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        exception(e)
    }
}

fun Activity.delayRun(block: () -> Unit, duration: Long) {
    window.decorView.postDelayed({
        if (!isFinishing) {
            block()
        }
    }, duration)
}

fun Fragment.delayRun(block: () -> Unit, duration: Long) {
    activity?.let {
        it.window.decorView.postDelayed({
            if (!it.isFinishing) {
                block()
            }
        }, duration)
    }
}
