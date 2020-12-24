package com.cwand.lib.ktx.repository

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : 网络请求接口服务
 *
 */
interface BaseNetworkApi {

    fun <T> getApi(apiClass: Class<T>, baseUrl: String = ""): T

    fun <T> getApi(apiClass: Class<T>): T

    fun getOkHttpClient(): OkHttpClient

    fun getRetrofit(baseUrl: String = ""): Retrofit
}