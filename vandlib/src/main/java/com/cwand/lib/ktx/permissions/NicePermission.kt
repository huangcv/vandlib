package com.cwand.lib.ktx.permissions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


/**
 * @author : chunwei
 * @date : 2021/3/15
 * @description :
 *
 */

private const val PERMISSION_REQUEST_CODE = 100

fun requestPermission(activity: FragmentActivity): PermissionBuilder =
    PermissionBuilder(RequestSource(activity))

class PermissionBuilder constructor(private val source: RequestSource) {

    fun permissions(vararg permission: String) = apply {
        source.permissions(*permission)
    }

    fun granted(action: () -> Unit) = apply {
        source.granted(action)
    }

    fun denied(action: (ArrayList<String>) -> Unit) = apply {
        source.denied(action)
    }

    fun explain(action: (ArrayList<String>) -> Unit) = apply {
        source.explain(action)
    }

    fun start() {
        source.start()
    }

}

class RequestSource constructor(private val target: FragmentActivity) {
    internal val permissions: ArrayList<String> = arrayListOf()
    internal var grantedAction: () -> Unit = {}
    internal var deniedAction: (ArrayList<String>) -> Unit = {}
    internal var explainAction: (ArrayList<String>) -> Unit = {}

    fun permissions(vararg permission: String) {
        permissions.addAll(permission)
    }

    fun granted(action: () -> Unit) {
        this.grantedAction = action
    }

    fun denied(action: (ArrayList<String>) -> Unit) {
        this.deniedAction = action
    }

    fun explain(action: (ArrayList<String>) -> Unit) {
        this.explainAction = action
    }

    fun start() {
        val fragmentManager: FragmentManager = target.supportFragmentManager
        var permissionFragment = fragmentManager.findFragmentByTag("NicePermissionFragment")
        if (permissionFragment == null) {
            permissionFragment = PermissionFragment(this)
            fragmentManager.beginTransaction().add(permissionFragment, "NicePermissionFragment")
                .commit()
            fragmentManager.executePendingTransactions()
        }
        if (permissionFragment is PermissionFragment) {
            permissionFragment.startRequest()
        }
    }

}

internal class PermissionFragment constructor(private val requestSource: RequestSource) :
    Fragment() {

    fun startRequest() {
        requestPermissions(
            requestSource.permissions.toArray(arrayOf<String>()),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //已授权
            val grantedList = arrayListOf<String>()
            //未授权
            val deniedList = arrayListOf<String>()
            //需要解释
            val explainList = arrayListOf<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    grantedList.add(permissions[index])
                } else {
                    val shouldShowReason =
                        shouldShowRequestPermissionRationale(permissions[index])
                    if (shouldShowReason) {
                        //用户拒绝权限,但未勾选不在询问,可以再次申请
                        explainList.add(permissions[index])
                    } else {
                        //永久被拒
                        deniedList.add(permissions[index])
                    }
                }
            }
            //是否已授权所有
            if (grantedList.size == permissions.size) {
                //已授权所有
                requestSource.grantedAction.invoke()
            } else {
                //有未授权的权限
                if (explainList.isNotEmpty()) {
                    //可以重新授权
                    requestSource.explainAction.invoke(explainList)
                } else {
                    //永远被拒
                    if (deniedList.isNotEmpty()) {
                        requestSource.deniedAction.invoke(deniedList)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
