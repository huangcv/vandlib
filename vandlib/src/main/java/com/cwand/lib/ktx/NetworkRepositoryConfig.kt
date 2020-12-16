package com.cwand.lib.ktx

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : 网络请求配置
 *
 */
class NetworkRepositoryConfig private constructor(private val builder: Builder) {

    val readTimeout: Long
        get() = builder.readTimeout

    val writeTimeout: Long
        get() = builder.writeTimeout

    val connectTimeout: Long
        get() = builder.connectTimeout

    val retryConnectCount: Int
        get() = builder.retryConnectCountWhenFailed

    val baseUrl: String
        get() = builder.baseUrl


    class Builder {
        //读取超时时间
        var readTimeout: Long = 10L //单位:秒
            private set

        //写入超时时间
        var writeTimeout: Long = 10L //单位:秒
            private set

        //连接超时时间
        var connectTimeout: Long = 10L //单位:秒
            private set

        //失败重连次数,当为0时,不进行重连
        var retryConnectCountWhenFailed: Int = 0
            private set

        var baseUrl: String = "https://run.mocky.io/"
            private set


        fun readTimeout(rt: Long): Builder {
            this.readTimeout = rt
            return this
        }

        fun writeTimeout(wt: Long): Builder {
            this.writeTimeout = wt
            return this
        }

        fun connectTimeout(ct: Long): Builder {
            this.connectTimeout = ct
            return this
        }

        fun retryConnectCount(count: Int): Builder {
            this.retryConnectCountWhenFailed = count
            return this
        }

        fun baseUrl(bu: String): Builder {
            this.baseUrl = bu
            return this
        }

        fun build(): NetworkRepositoryConfig {
            return NetworkRepositoryConfig(this)
        }

    }

}