package com.cwand.lib.ktx.ext

import java.util.regex.Pattern

private val cache = arrayOf(
    "*", "**", "***",
    "****", "*****", "******",
    "*******", "********", "*********",
    "**********", "***********", "************",
    "*************", "**************", "***************",
    "****************", "*****************", "******************",
    "*******************", "********************", "*********************",
    "**********************")
private const val coverDefault = '*'

private const val EMAIL_PATTERN = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"
private const val PHONE_PATTERN = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"

fun String?.isEmail(): Boolean {
    return this?.let {
        Pattern.matches(EMAIL_PATTERN, it)
    } ?: false
}

fun String?.isPhone(): Boolean {
    return this?.let {
        Pattern.matches(PHONE_PATTERN, it)
    } ?: false
}

fun String?.replaceBlank(): String {
    return this?.replace("\\s+", "") ?: ""
}

fun String?.mask(startIndex: Int, endIndex: Int): String {
    return mask(startIndex, endIndex, coverDefault)
}

fun String?.mask(startIndex: Int, endIndex: Int, coverStr: Char): String {
    return this?.let {
        return if (startIndex >= it.length || endIndex >= it.length) {
            cache[it.length - 1]
        } else {
            it.substring(0, startIndex - 1).plus(doCover(it, startIndex, endIndex, coverStr))
                .plus(it.substring(endIndex))
        }
    } ?: ""
}

private fun doCover(source: String, startIndex: Int, endIndex: Int, coverStr: Char): String {
    var tempStartIndex = startIndex
    var tempEndIndex = endIndex
    if (tempStartIndex < 0 || tempEndIndex < 0) return ""
    if (tempStartIndex > tempEndIndex) {
        //如果开始和结束位置写反了,则会自动容错
        tempStartIndex = tempStartIndex xor tempEndIndex
        tempEndIndex = tempEndIndex xor tempStartIndex
        tempStartIndex = tempStartIndex xor tempEndIndex
    }
    //优先使用缓存替换
    if (tempEndIndex - tempStartIndex < cache.size && coverStr == coverDefault) return cache[tempEndIndex - tempStartIndex]
    //掩码长度大于16位, 则循环遍历替换
    val sb: StringBuilder = StringBuilder(tempEndIndex - tempStartIndex)
    while (tempStartIndex <= tempEndIndex) {
        sb.append(coverStr)
        tempStartIndex++
    }
    return sb.toString()
}
