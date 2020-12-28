package com.cwand.lib.ktx.extensions

import android.util.Log

/**
 * @author : chunwei
 * @date : 2020/12/7
 * @description : 日志工具扩展
 *
 */

const val TAG = "VandLib"

var showLog = true

var defLevel = Level.E

enum class Level(val level: Int) {
    V(5),
    D(4),
    I(3),
    W(2),
    E(1)
}

fun Any.logD(tag: String = TAG) {
    if (this is Throwable) {
        log(tag, "", Level.D, this)
    } else {
        log(tag, this, Level.D)
    }
}

fun Any.logE(tag: String = TAG) {
    if (this is Throwable) {
        log(tag, "", Level.E, this)
    } else {
        log(tag, this, Level.E)
    }
}

fun Any.logI(tag: String = TAG) {
    if (this is Throwable) {
        log(tag, "", Level.I, this)
    } else {
        log(tag, this, Level.I)
    }
}

fun Any.logV(tag: String = TAG) {
    if (this is Throwable) {
        log(tag, "", Level.V, this)
    } else {
        log(tag, this, Level.V)
    }
}

fun Any.logW(tag: String = TAG) {
    if (this is Throwable) {
        log(tag, "", Level.W, this)
    } else {
        log(tag, this, Level.W)
    }
}

private fun log(tag: String, msg: Any, levelTarget: Level, throwable: Throwable? = null) {
    if (!showLog) {
        return
    }
    if (levelTarget.level < defLevel.level) {
        return
    }
    val stackTrace = getTargetStackTraceElement()
    when (levelTarget) {
        Level.V -> {
            Log.v(tag,
                if (stackTrace == null) msg.toString() else "(${stackTrace.fileName}:${stackTrace.lineNumber}) ---> ".plus(
                    msg.toString()), throwable)
        }
        Level.D -> {
            Log.d(tag,
                if (stackTrace == null) msg.toString() else "(${stackTrace.fileName}:${stackTrace.lineNumber}) ---> ".plus(
                    msg.toString()), throwable)
        }
        Level.I -> {
            Log.i(tag,
                if (stackTrace == null) msg.toString() else "(${stackTrace.fileName}:${stackTrace.lineNumber}) ---> ".plus(
                    msg.toString()), throwable)
        }
        Level.W -> {
            Log.w(tag,
                if (stackTrace == null) msg.toString() else "(${stackTrace.fileName}:${stackTrace.lineNumber}) ---> ".plus(
                    msg.toString()), throwable)
        }
        Level.E -> {
            Log.e(tag,
                if (stackTrace == null) msg.toString() else "(${stackTrace.fileName}:${stackTrace.lineNumber}) ---> ".plus(
                    msg.toString()), throwable)
        }
    }
}

private fun getTargetStackTraceElement(): StackTraceElement? {
    var traceElement: StackTraceElement? = null
    val stackTrace = Thread.currentThread().stackTrace
    for (stackTraceElement in stackTrace) {
        if (stackTraceElement != null) {
            if (stackTraceElement.fileName != null) {
                if (stackTraceElement.fileName == "VMStack.java" || stackTraceElement.fileName == "Thread.java" || stackTraceElement.fileName == "LogExt.kt") {
                    continue
                }
            }
            traceElement = stackTraceElement
        }
        break
    }
    return traceElement
}
