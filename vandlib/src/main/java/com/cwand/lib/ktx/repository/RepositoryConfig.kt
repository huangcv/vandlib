package com.cwand.lib.ktx.repository

/**
 * @author : chunwei
 * @date : 2020/12/15
 * @description : 数据源配置
 *
 */
class RepositoryConfig private constructor() {

    companion object {
        var networkRepositoryConfig: NetworkRepositoryConfig =
            NetworkRepositoryConfig.Builder().build()
    }
}