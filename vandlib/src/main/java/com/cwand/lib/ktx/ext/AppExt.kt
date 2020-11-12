package com.cwand.lib.ktx.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

/**
 * 上下文获取屏幕宽度像素
 */
val Context.screenWidthPixels
    get() = resources.displayMetrics.widthPixels

/**
 * 上下文获取屏幕高度像素
 */
val Context.screenHeightPixels
    get() = resources.displayMetrics.heightPixels

/**
 * 像素转DP
 */
val Float.toDp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics)

/**
 * 像素转SP
 */
val Float.toSp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics)

/**
 * 像素转DP
 */
val Int.toDp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)

/**
 * 像素转SP
 */
val Int.toSp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)

/**
 * 像素转DP
 */
val Double.toDp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)

/**
 * 像素转SP
 */
val Double.toSp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)

/**
 * 保存到剪贴板
 */
fun Context.copyToClipboard(saveString: String, label: String = "VandLib") {
    val newPlainText = ClipData.newPlainText(label, saveString)
    clipboardManager?.setPrimaryClip(newPlainText)
}

/**
 * 获取剪贴板第一条数据
 */
fun Context.getClipboardString(): String {
    var result = ""
    clipboardManager?.primaryClip?.let {
        if (it.itemCount > 0) {
            result = it.getItemAt(0).text.toString()
        }
    }
    return result
}



