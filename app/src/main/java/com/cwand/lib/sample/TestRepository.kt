package com.cwand.lib.sample

import com.cwand.lib.ktx.repository.BaseRepository

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : Test数据仓库
 *
 */
class TestRepository : BaseRepository() {

    val api = getApi(TestApi::class.java)

    suspend fun getTestData(): List<TestBean> {
        return request {
            api.test(emptyMap())
        }
    }

}