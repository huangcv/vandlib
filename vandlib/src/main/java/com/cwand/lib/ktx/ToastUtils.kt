package com.cwand.lib.ktx

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtils {
    @JvmStatic
    fun toast(context: Context, message: CharSequence) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun toast(context: Context, @StringRes messageRes: Int) {
        Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }
}