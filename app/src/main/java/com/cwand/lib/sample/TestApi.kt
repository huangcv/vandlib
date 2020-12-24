package com.cwand.lib.sample

import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * @author : chunwei
 * @date : 2020/12/14
 * @description : 测试用Api
 *
 */
interface TestApi {
    @Headers("key:value", "key:value")
//    @POST("https://run.mocky.io/v3/fde178be-e4f7-4013-841f-de21ee3b6c9e")
    @POST("https://run.mocky.io/v3/2459828c-865f-461a-b354-107c4aec650b")
//    @POST("https://run.mocky.io/v3/0d76f9e3-6d76-4f23-b34f-1d5eddf0fdc0")
//    @POST("https://run.mocky.io/v3/8a4f40cf-276d-4d88-bde1-571fc8293cdd")
    @FormUrlEncoded
    suspend fun test(@FieldMap params: Map<String, String>): ApiResponse<List<TestBean>>
}