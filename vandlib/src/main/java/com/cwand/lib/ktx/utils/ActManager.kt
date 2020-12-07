package com.cwand.lib.ktx.utils

import android.app.Activity
import android.nfc.Tag
import android.text.TextUtils
import com.cwand.lib.ktx.ext.safeRun
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 统一管理所有Activity的工具类
 *
 */
class ActManager private constructor() {

    private val activityStack: Stack<Activity> by lazy { Stack<Activity>() }
    private val aliasStack: Stack<String> by lazy { Stack<String>() }

    companion object {

        private val instance: ActManager = Holder.holder

        /**
         * 添加Activity
         * @param activity
         */
        @JvmStatic
        fun add(activity: Activity) {
            instance.addActivity(activity)
        }

        /**
         * 按照别名添加Activity入栈
         * @param activity
         * @param alias
         */
        @JvmStatic
        fun add(activity: Activity, alias: String) {
            instance.addActivityWithAlias(activity, alias)
        }

        /**
         * 移除Activity,但不finish
         * @param activity
         */
        @JvmStatic
        fun remove(activity: Activity) {
            instance.removeActivity(activity)
        }

        /**
         * 根据别名从栈中移除,但不finish
         */
        @JvmStatic
        fun removeByAlias(alias: String) {
            instance.removeActivityByAlias(alias)
        }

        /**
         * 移除栈顶的Activity,但不finish
         */
        @JvmStatic
        fun removeTop() {
            instance.removeTopActivity()
        }

        /**
         * 关闭栈顶的Activity并且会从栈中移除
         */
        @JvmStatic
        fun finishTop(): Boolean {
            return instance.finishTopActivity()
        }

        /**
         * 关闭栈中所有Activity并且全部从栈中移除
         */
        @JvmStatic
        fun finishAll() {
            instance.finishAllActivity()
        }

        /**
         * 关闭指定别名的Activity并且从栈中移除
         * @param alias
         */
        @JvmStatic
        fun finishByAlias(alias: String): Boolean {
            return instance.finishActivityByAlias(alias)
        }

        /**
         * 根据别名获取Activity
         * @param alias
         * @return 目标Activity
         */
        @JvmStatic
        fun getByAlias(alias: String): Activity? {
            return instance.getActivityByAlias(alias)
        }

        /**
         * 获取栈顶的Activity
         */
        @JvmStatic
        fun getTop(): Activity {
            return instance.getTopActivity()
        }

        /**
         * 获取栈底的Activity
         */
        @JvmStatic
        fun getLast(): Activity {
            return instance.getLastActivity()
        }

        /**
         * 关闭并移除栈中所有的Activity,除了指定的Activity
         */
        @JvmStatic
        fun finishAllExclude(activity: Activity): Boolean {
            return instance.finishAllExcludeActivity(activity)
        }

        /**
         * 关闭并移除栈中所有的Activity,除了指定别名的Activity
         */
        @JvmStatic
        fun finishAllExclude(alias: String): Boolean {
            return instance.finishAllExcludeActivityByAlias(alias)
        }
    }

    private object Holder {
        val holder = ActManager()
    }

    private fun addActivity(activity: Activity) {
        addActivityWithAlias(activity, activity::class.java.simpleName)
    }

    @Synchronized
    private fun addActivityWithAlias(activity: Activity, alias: String) {
        var isAddSuccess = false
        var isAliasAddSuccess = false
        try {
            if (activityStack.add(activity)) {
                isAddSuccess = true
                aliasStack.add(alias)
                isAliasAddSuccess = true
            }
        } catch (e: Exception) {
            if (isAddSuccess) {
                activityStack.remove(activity)
            }
            if (isAliasAddSuccess) {
                aliasStack.remove(alias)
            }
        }
        println("添加Activity: $activity , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    private fun removeActivity(activity: Activity) {
        val alias = activity::class.java.simpleName
        if (activityStack.remove(activity)) {
            aliasStack.remove(alias)
        }
        println("删除Activity $activity , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    private fun removeActivityByAlias(alias: String) {
        val index = aliasStack.indexOfFirst {
            alias.equals(it, true)
        }
        if (index >= 0) {
            aliasStack.removeElementAt(index)
            activityStack.removeElementAt(index)
        }
        println("删除别名: $alias 的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    private fun removeTopActivity() {
        aliasStack.pop()
        activityStack.pop()
        println("移除栈顶的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    private fun finishActivityByAlias(alias: String): Boolean {
        try {
            val index = aliasStack.indexOfFirst {
                alias.equals(it, true)
            }
            if (index >= 0 && index < aliasStack.size && index < activityStack.size) {
                aliasStack.removeElementAt(index)
                activityStack.removeAt(index).finish()
            }
            println("关闭别名: $alias 的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun finishTopActivity(): Boolean {
        try {
            aliasStack.pop()
            activityStack.pop().finish()
            println("关闭栈顶的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun finishAllActivity() {
        aliasStack.clear()
        activityStack.removeAll {
            it.finish()
            true
        }
        println("关闭所有Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    private fun getActivityByAlias(alias: String): Activity? {
        println("获取别名: $alias 的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        try {
            val index = aliasStack.indexOfFirst {
                alias.equals(it, true)
            }
            if (index >= 0 && index < aliasStack.size && index < activityStack.size) {
                return activityStack[index]
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun getTopActivity(): Activity {
        println("获取栈顶的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        return activityStack.peek()
    }

    private fun getLastActivity(): Activity {
        println("获取栈底的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        return activityStack.lastElement()
    }

    private fun finishAllExcludeActivity(activity: Activity): Boolean {
        try {
            activityStack.removeAll {
                val b = it != activity
                if (b) it.finish()
                b
            }
            activityStack.clear()
            aliasStack.clear()
            addActivity(activity)
            println("移除所有的Activity ,除了 $activity . 当前Activity栈详情: $activityStack , $aliasStack")
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun finishAllExcludeActivity(activity: Activity, alias: String): Boolean {
        try {
            activityStack.removeAll {
                val b = it != activity
                if (b) it.finish()
                b
            }
            activityStack.clear()
            aliasStack.clear()
            addActivityWithAlias(activity, alias)
            println("移除所有的Activity ,除了别名: $alias 的Activity. 当前Activity栈详情: $activityStack , $aliasStack")
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun finishAllExcludeActivityByAlias(alias: String): Boolean {
        try {
            val index = aliasStack.indexOfFirst {
                alias.equals(it, true)
            }
            if (index >= 0 && index < aliasStack.size && index < activityStack.size) {
                val activity = activityStack[index]
                finishAllExcludeActivity(activity, alias)
            }
        } catch (e: Exception) {
        }
        return true
    }

}

fun ActManager.transactionRun(
    runBlock: () -> Unit,
    success: () -> Unit = {},
    failed: (java.lang.Exception) -> Unit = { },
) {
    val reentrantLock = ReentrantLock()
    try {
        reentrantLock.lock()
        runBlock()
        reentrantLock.unlock()
        success()
    } catch (e: Exception) {
        failed(e)
    } finally {
        reentrantLock.unlock()
    }
}

