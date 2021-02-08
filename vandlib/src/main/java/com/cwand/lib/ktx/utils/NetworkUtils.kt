package com.cwand.lib.ktx.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.cwand.lib.ktx.extensions.connectivityManager

/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : 网络工具类
 *
 */
class NetworkUtils {

    companion object {

        @JvmStatic
        @WorkerThread
        fun networkConnected(): Boolean {
            return PingUtils.pingTest()
        }

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean {
            return getNetworkInfo(context)?.isAvailable ?: false
        }

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        @JvmStatic
        fun getNetworkState(context: Context): NetworkState {
            getNetworkInfo(context)?.let {
                if (it.isAvailable) {
                    if (it.type == ConnectivityManager.TYPE_WIFI) {
                        return@getNetworkState NetworkState.WIFI
                    } else if (it.type == ConnectivityManager.TYPE_MOBILE) {
                        return@getNetworkState when (it.subtype) {
                            TelephonyManager.NETWORK_TYPE_GSM,
                            TelephonyManager.NETWORK_TYPE_GPRS,
                            TelephonyManager.NETWORK_TYPE_CDMA,
                            TelephonyManager.NETWORK_TYPE_EDGE,
                            TelephonyManager.NETWORK_TYPE_1xRTT,
                            TelephonyManager.NETWORK_TYPE_IDEN,
                            -> NetworkState.NETWORK_2G
                            TelephonyManager.NETWORK_TYPE_TD_SCDMA,
                            TelephonyManager.NETWORK_TYPE_EVDO_A,
                            TelephonyManager.NETWORK_TYPE_UMTS,
                            TelephonyManager.NETWORK_TYPE_EVDO_0,
                            TelephonyManager.NETWORK_TYPE_HSDPA,
                            TelephonyManager.NETWORK_TYPE_HSUPA,
                            TelephonyManager.NETWORK_TYPE_HSPA,
                            TelephonyManager.NETWORK_TYPE_EVDO_B,
                            TelephonyManager.NETWORK_TYPE_EHRPD,
                            TelephonyManager.NETWORK_TYPE_HSPAP,
                            -> NetworkState.NETWORK_3G
                            TelephonyManager.NETWORK_TYPE_IWLAN,
                            TelephonyManager.NETWORK_TYPE_LTE,
                            -> NetworkState.NETWORK_4G
                            else -> when (it.subtypeName) {
                                "TD-SCDMA", "WCDMA", "CDMA2000" -> return@getNetworkState NetworkState.NETWORK_3G
                                else -> return@getNetworkState NetworkState.NETWORK_UNKNOWN
                            }
                        }
                    }
                }
            }
            return NetworkState.NETWORK_NO
        }

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        @JvmStatic
        fun getNetworkInfo(context: Context): NetworkInfo? {
            return context.connectivityManager?.activeNetworkInfo
        }

    }
}