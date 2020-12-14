package com.cwand.lib.sample

import retrofit2.Call
import retrofit2.http.*

/**
 * @author : chunwei
 * @date : 2020/12/14
 * @description : 测试用Api
 *
 */
interface TestApi {
    @Headers("key:value", "key:value")
    @POST("https://run.mocky.io/v3/8fe6bceb-3bd1-4cc3-85cf-be4cc12cc93c")
    @FormUrlEncoded
    open fun test(@FieldMap params: Map<String, String>): Call<TestData>?
}