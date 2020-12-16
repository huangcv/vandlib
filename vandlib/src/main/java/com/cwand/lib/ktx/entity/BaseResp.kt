package com.cwand.lib.ktx.entity

/**
 * 服务器返回数据格式的基类
 * 如果需要框架做统一处理,需要继承此类,然后必须实现抽象方法
 */
class BaseResp<T>(val code: Int, val msg: String, val result: T?) {
}