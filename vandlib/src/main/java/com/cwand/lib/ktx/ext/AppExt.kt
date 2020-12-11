package com.cwand.lib.ktx.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.TypedValue

private const val APP_DEFAULT_SP_NAME = "AppSharedPreferencesConfig"
private const val DEFAULT_OTHER_SP_NAME = "NewSharedPreferencesConfig"
private const val CLIPBOARD_DEFAULT_LABEL = "VandLib"

//缓存的App配置的SharedPreferences对象
private var sp: SharedPreferences? = null

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
 * 获取App配置的SharedPreferences对象,带缓存
 */
fun Context.getAppSp(spName: String = APP_DEFAULT_SP_NAME): SharedPreferences {
    if (sp == null) {
        sp = this.applicationContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }
    return sp!!
}

/**
 * 获取一个指定sp name的新的SharedPreferences对象
 */
fun Context.getNewSp(spName: String = DEFAULT_OTHER_SP_NAME): SharedPreferences {
    return this.applicationContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
}

/**
 * 保存其他配置的sp值
 */
fun Context.putSpValue(
    key: String,
    value: String,
    spName: String = DEFAULT_OTHER_SP_NAME,
): Boolean {
    return getNewSp(spName).edit().putString(key, value).commit()
}

/**
 * 获取其他配置的sp值
 */
fun Context.getSpValue(key: String, spName: String = DEFAULT_OTHER_SP_NAME): String {
    return getNewSp(spName).getString(key, "") ?: ""
}

/**
 * 保存App配置的sp值
 */
fun Context.putAppSpValue(
    key: String,
    value: String,
): Boolean {
    return getAppSp().edit().putString(key, value).commit()
}

/**
 * 获取App配置的sp值
 */
fun Context.getAppSpValue(key: String): String {
    return getAppSp().getString(key, "") ?: ""
}

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
 * 复制到剪贴板
 */
fun Context.copyToClipboard(saveString: String, label: String = CLIPBOARD_DEFAULT_LABEL) {
    val newPlainText = ClipData.newPlainText(label, saveString)
    clipboardManager?.setPrimaryClip(newPlainText)
}

/**
 * 获取剪贴板指定角标的数据
 */
fun Context.getClipboardString(index: Int = 0): String {
    var result = ""
    clipboardManager?.primaryClip?.let {
        if (it.itemCount > 0) {
            result = it.getItemAt(index).text.toString()
        }
    }
    return result
}



