package com.cwand.lib.ktx.repository

import com.cwand.lib.ktx.entity.BaseResponse

interface IDataRepository {
    suspend fun <T> request(block: suspend () -> BaseResponse<T>): T
    suspend fun <T, R> T.parse(block: suspend (T) -> R): R

}