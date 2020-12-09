package com.cwand.lib.ktx

import com.cwand.lib.ktx.entity.BaseResp

interface IDataRepository {
    suspend fun <T> request(block: suspend () -> BaseResp<T?>): BaseResp<T?>
    suspend fun <T, R> T.parse(block: suspend (T) -> R?): R?

}