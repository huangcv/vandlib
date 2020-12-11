package com.cwand.lib.ktx.utils

import androidx.annotation.WorkerThread
import com.cwand.lib.ktx.ext.logD
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * @author : chunwei
 * @date : 2020/12/10
 * @description : Ping工具类
 *
 */
class PingUtils {

    companion object {
        //百度的IP
        private const val test_ip = "61.135.169.125"

        @JvmStatic
        @WorkerThread
        fun ping(
            ip: String,
            retryCount: Int = 3,
            timeoutSeconds: Int = 3,
            showLog: Boolean = false,
        ): Boolean {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            var result = false
            try {
                val pingCommendLine = "ping -c $retryCount -w $timeoutSeconds $ip"
                val process = Runtime.getRuntime().exec(pingCommendLine)
                val status = process.waitFor()
                result = when (status) {
                    0 -> {
                        if (showLog) {
                            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                            val stringBuffer = StringBuffer()
                            var line: String?
                            while (true) {
                                line = bufferedReader.readLine()
                                if (line == null) {
                                    break
                                }
                                stringBuffer.append(line).append("\n")
                            }
                            stringBuffer.toString().logD("PingUtils")
                        }
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = false
            }
            return result
        }

        @JvmStatic
        @WorkerThread
        fun pingTest(): Boolean {
            return ping(test_ip)
        }
    }

}