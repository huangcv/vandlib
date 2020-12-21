package com.cwand.lib.ktx.utils

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * @author : chunwei
 * @date : 2020/12/3
 * @description : 统一管理Activity的工具类
 *
 */
class ActManager private constructor() {

    private val activityStack: Stack<Activity> by lazy { Stack<Activity>() }
    private val aliasStack: Stack<String> by lazy { Stack<String>() }
    private var isAppInBackground = false

    companion object {

        private val instance: ActManager = Holder.holder

        @JvmStatic
        fun registerApplicationCallback(application: Application) {
            instance.registerApplicationCallbackInner(application)
        }

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

        /**
         * 重启指定别名的Activity
         */
        @JvmStatic
        fun restartActivity(alias: String) {
            instance.restartActivityAlias(alias)
        }
    }

    /**
     * 注册监听回调
     */
    private fun registerApplicationCallbackInner(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private object Holder {
        val holder = ActManager()
    }

    /**
     * 添加Activity
     */
    private fun addActivity(activity: Activity) {
        addActivityWithAlias(activity, activity::class.java.simpleName)
    }

    /**
     * 添加Activity并指定别名
     */
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

    /**
     * 移除指定的Activity且不关闭
     */
    private fun removeActivity(activity: Activity) {
        val alias = activity::class.java.simpleName
        if (activityStack.remove(activity)) {
            aliasStack.remove(alias)
        }
        println("删除Activity $activity , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    /**
     * 移除指定别名的Activity且不关闭
     */
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

    /**
     * 移除栈顶Activity且不关闭
     */
    private fun removeTopActivity() {
        aliasStack.pop()
        activityStack.pop()
        println("移除栈顶的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    /**
     * 关闭指定别名的Activity
     */
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

    /**
     * 关闭栈顶Activity
     */
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

    /**
     * 关闭所有的Activity
     */
    private fun finishAllActivity() {
        aliasStack.clear()
        activityStack.removeAll {
            it.finish()
            true
        }
        println("关闭所有Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
    }

    /**
     * 通过别名获取Activity
     */
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

    /**
     * 获取栈顶Activity
     */
    private fun getTopActivity(): Activity {
        println("获取栈顶的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        return activityStack.peek()
    }

    /**
     * 获取栈底Activity
     */
    private fun getLastActivity(): Activity {
        println("获取栈底的Activity  , 当前Activity栈详情: $activityStack , $aliasStack")
        return activityStack.lastElement()
    }

    /**
     * 关闭所有的Activity除了指定的Activity
     */
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

    /**
     * 关闭所有的Activity除了指定的Activity
     */
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

    /**
     * 关闭所有的Activity除了指定别名的Activity
     */
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

    /**
     * 重启指定别名的Activity
     */
    private fun restartActivityAlias(alias: String) {
        getActivityByAlias(alias)?.let {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                // 6.0 以及以下版本，使用这种方式，并给 activity 添加启动动画效果，可以规避黑屏和闪烁问题
                val intent = Intent(it, it::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                it.startActivity(intent)
                it.finish()
            } else {
                // 6.0 以上系统直接调用重新创建函数，可以达到无缝切换的效果
                it.recreate()
            }
        }
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

