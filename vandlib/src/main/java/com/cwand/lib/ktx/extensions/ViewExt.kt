package com.cwand.lib.ktx.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

fun View.onClick(block: (View) -> Unit) {
    this.setOnClickListener {
        block(it)
    }
}

var lastClickedTime = 0L

/**
 * 300ms内view 不可以再次点击
 */
fun View.onIntervalClick(interval: Long = 300L, block: (View) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickedTime > interval) {
            lastClickedTime = currentTimeMillis
            block(it)
        }
    }
}

/**
 * 隐藏View
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 显示View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * 隐藏View并保留其原始位置
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

fun EditText.getString(): String {
    return this.text.toString().trim()
}

fun EditText.isEmpty(): Boolean {
    return this.getString().isEmpty()
}

fun TextView.getString(): String {
    return this.text.toString().trim()
}

fun TextView.isEmpty(): Boolean {
    return this.getString().isEmpty()
}

fun EditText.afterTextChanged(block: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            block(s.toString())
        }
    })
}