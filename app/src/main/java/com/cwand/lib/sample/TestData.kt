package com.cwand.lib.sample

/**
 * @author : chunwei
 * @date : 2020/12/14
 * @description :
 *
 */

data class TestData(
    val `data`: List<Data>,
    val time: String
)

data class Data(
    val code: Int,
    val name: String
)