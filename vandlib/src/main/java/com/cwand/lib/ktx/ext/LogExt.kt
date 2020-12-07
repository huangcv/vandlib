package com.cwand.lib.ktx.ext

import android.util.Log
import com.cwand.lib.ktx.utils.Logger

/**
 * @author : chunwei
 * @date : 2020/12/7
 * @description : 日志工具扩展
 *
 */

const val TAG = "VandLib"

var showLog = true

var level = Level.D

enum class Level(val level: Int) {
    V(5),
    D(4),
    I(3),
    W(2),
    E(1)
}
fun String.logD(tag: String = TAG) {
    log(tag, this, Level.D)
}

fun String.logE(tag: String = TAG) {
    log(tag, this, Level.E)
}

fun String.logI(tag: String = TAG) {
    log(tag, this, Level.I)
}

fun String.logV(tag: String = TAG) {
    log(tag, this, Level.V)
}

fun String.logW(tag: String = TAG) {
    log(tag, this, Level.W)
}


fun Throwable.logD(tag: String = TAG) {
    log(tag, "", Level.D, this)
}

fun Throwable.logE(tag: String = TAG) {
    log(tag, "", Level.E, this)
}

fun Throwable.logI(tag: String = TAG) {
    log(tag, "", Level.I, this)
}

fun Throwable.logV(tag: String = TAG) {
    log(tag, "", Level.V, this)
}

fun Throwable.logW(tag: String = TAG) {
    log(tag, "", Level.W, this)
}

fun Throwable.empty():Throwable {
    return Throwable()
}

private fun log(tag: String, msg: Any, levelTarget: Level, throwable: Throwable? = null) {
    if (!showLog) {
        return
    }
    if (levelTarget.level < level.level) {
        return
    }
    val stackTrace = getTargetStackTraceElement()
    when (level) {
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
//            if (stackTraceElement.className != null) {
//                if (stackTraceElement.className == String::class.java.name) {
//                    continue
//                }
//            }
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
