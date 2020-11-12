package com.cwand.lib.ktx.entity

/**
 * 服务器返回数据格式的基类
 * 如果需要框架做统一处理,需要继承此类,然后必须实现抽象方法
 */
abstract class BaseResp<T> {

    /**
     * 响应码
     */
    abstract fun responseCode(): Int

    /**
     * 响应消息
     */
    abstract fun responseMsg(): String

    /**
     * 响应数据
     */
    abstract fun responseData(): T

    /**
     * 请求是否成功
     */
    abstract fun isOK(): Boolean

}