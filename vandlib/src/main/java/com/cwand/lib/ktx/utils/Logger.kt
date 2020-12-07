package com.cwand.lib.ktx.utils

import android.util.Log

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 日志输出工具
 *
 */
class Logger {

    enum class Level(val level: Int) {
        V(5),
        D(4),
        I(3),
        W(2),
        E(1)
    }

    companion object {
        var isShowLog = true
        var defaultTag = "Logger"
        private fun log(tag: String, msg: Any, throwable: Throwable?, level: Level) {
            if (!isShowLog) {
                return
            }
            if (level.level < Level.E.level) {
                return
            }
            val stackTrace = getTargetStackTraceElement()
            when (level) {
                Level.V -> {
                    Log.v(tag,
                        if (stackTrace == null) msg.toString() else  "( ${stackTrace.fileName} : ${stackTrace.lineNumber} ) ---> ".plus(msg.toString()),
                        throwable)
                }
                Level.D -> {
                    Log.d(tag,
                        if (stackTrace == null) msg.toString() else  "( ${stackTrace.fileName} : ${stackTrace.lineNumber} ) ---> ".plus(msg.toString()),
                        throwable)
                }
                Level.I -> {
                    Log.i(tag,
                        if (stackTrace == null) msg.toString() else  "( ${stackTrace.fileName} : ${stackTrace.lineNumber} ) ---> ".plus(msg.toString()),
                        throwable)
                }
                Level.W -> {
                    Log.w(tag,
                        if (stackTrace == null) msg.toString() else  "( ${stackTrace.fileName} : ${stackTrace.lineNumber} ) ---> ".plus(msg.toString()),
                        throwable)
                }
                Level.E -> {
                    Log.e(tag,
                        if (stackTrace == null) msg.toString() else  "( ${stackTrace.fileName} : ${stackTrace.lineNumber} ) ---> ".plus(msg.toString()),
                        throwable)
                }
            }
        }

        private fun getTargetStackTraceElement(): StackTraceElement? {
            var traceElement: StackTraceElement? = null
            val stackTrace = Thread.currentThread().stackTrace
            for (stackTraceElement in stackTrace) {
                if (stackTraceElement != null) {
                    if (stackTraceElement.className != null) {
                        if (stackTraceElement.className == Logger::class.java.name) {
                            continue
                        }
                    }
                    if (stackTraceElement.fileName != null) {
                        if (stackTraceElement.fileName == "VMStack.java" || stackTraceElement.fileName == "Thread.java") {
                            continue
                        }
                    }
                    traceElement = stackTraceElement
                }
                break
            }
            return traceElement
        }

    }


}