package com.ruanmeng.utils

import android.app.Activity
import android.os.Build
import android.support.annotation.RequiresApi

import java.util.Stack

class ActivityStack private constructor() {

    /**
     * 移除栈顶的activity
     */
    fun popActivity() {
        val activity = mActivityStack!!.lastElement()
        activity?.finish()
    }

    /**
     * 移除一个activity
     */
    private fun popActivity(activity: Activity?) {
        if (activity != null) {
            activity.finish()
            mActivityStack!!.remove(activity)
        }
    }

    /**
     * 获取栈顶的activity，先进后出原则
     */
    fun currentActivity(): Activity? {
        // lastElement()获取最后个子元素，这里是栈顶的Activity
        return if (mActivityStack == null || mActivityStack!!.size == 0) {
            null
        } else mActivityStack!!.lastElement()
    }

    /**
     * 将当前Activity推入栈中
     */
    fun pushActivity(activity: Activity) {
        if (mActivityStack == null) {
            mActivityStack = Stack()
        }
        mActivityStack!!.add(activity)
    }

    /**
     * 是否包含指定的Activity
     */
    fun isContainsActivity(cls: Class<*>): Boolean {
        if (mActivityStack == null || mActivityStack!!.size == 0) {
            return false
        }
        for (activity in mActivityStack!!) {
            if (activity.javaClass == cls && !activity.isDestroyed) {
                return true
            }
        }
        return false
    }

    /**
     * 弹出栈中指定Activity
     */
    fun popOneActivity(cls: Class<*>): Boolean {
        if (mActivityStack == null || mActivityStack!!.size == 0) return false
        for (activity in mActivityStack!!) {
            if (activity.javaClass == cls) {
                if (!activity.isDestroyed) {
                    popActivity(activity)
                    return true
                } else
                    popActivity()
            }
        }
        return false
    }

    /**
     * 弹出栈中所有Activity，保留指定的一个Activity
     */
    fun popAllActivityExceptOne(cls: Class<*>) {
        while (true) {
            val activity = currentActivity() ?: break
            if (activity.javaClass == cls) break
            popActivity(activity)
        }
    }

    /**
     * 移除指定的多个activity
     */
    fun popActivities(vararg clss: Class<*>) {
        for (cls in clss) {
            if (isContainsActivity(cls))
                popOneActivity(cls)
        }
    }

    /**
     * 弹出栈中所有Activity，保留指定的Activity
     */
    fun popAllActivityExcept(vararg clss: Class<*>) {
        for (i in mActivityStack!!.indices.reversed()) {
            val activity = mActivityStack!![i]
            var isNotFinish = false
            for (cls in clss) {
                if (activity.javaClass == cls) isNotFinish = true
            }
            if (!isNotFinish) popActivity(activity)
        }
    }

    /**
     * 弹出栈中所有Activity
     */
    fun popAllActivitys() {
        while (true) {
            val activity = currentActivity() ?: break
            popActivity(activity)
        }
    }

    companion object {

        /**
         * 注意：mActivityStack 中包含已经 finished 的 activity
         */
        private var mActivityStack: Stack<Activity>? = null
        private var instance: ActivityStack? = null

        val screenManager: ActivityStack
            get() {
                if (instance == null) {
                    instance = ActivityStack()
                }
                return instance!!
            }
    }

}
