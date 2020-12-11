package com.cwand.lib.ktx.interceptors.log

/**
 * @author : chunwei
 * @date : 2020/12/11
 * @description : 日志拦截器过滤等级
 * 除了 {NONE} 都会打印请求地址和请求方式例如:https://www.xxx.com/api/getAll POST,目的是为了区分日志数据
 *
 */
enum class LogLevel {
    //不打印日志
    NONE,

    //只打印请求头
    //包含:url,method,headers
    HEADERS,

    //只打印请求体
    //包含:url,method,body
    BODY,

    //打印所有内容
    //包含:url,method,headers,body
    ALL;

}