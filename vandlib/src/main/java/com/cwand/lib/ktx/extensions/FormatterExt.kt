package com.cwand.lib.ktx.extensions

import com.cwand.lib.ktx.extensions.FormatterExt.DECIMAL_FORMAT
import com.cwand.lib.ktx.extensions.FormatterExt.DEFAULT_PATTERN
import com.cwand.lib.ktx.extensions.FormatterExt.defaultSDF
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : chunwei
 * @date : 2020/12/24
 * @description :格式化工具扩展
 *
 */

fun main() {
    println(1.201111111.formatDecimal(4))
    println("1.0".deleteZero())
    println(System.currentTimeMillis().toString().formatDate())
    println(3.formatWeek())
    println(1.fillZeroWhenSingleDigit())
    println(189098089070.9901921.toStorageSize())
    println("11798203781.121819038".toFinancialNumber())
}

object FormatterExt {
    val DEFAULT_PATTERN by lazy { "yyyy-MM-dd HH:mm:ss" }
    val DEFAULT_CALENDAR by lazy { Calendar.getInstance() }
    var defaultSDF = SimpleDateFormat(DEFAULT_PATTERN)
    val DECIMAL_FORMAT by lazy { DecimalFormat() }
}


fun FormatterExt.updateLocale() {
    defaultSDF = SimpleDateFormat(DEFAULT_PATTERN)
}

fun Long.formatData(pattern: String = DEFAULT_PATTERN):String{
    defaultSDF.applyPattern(pattern)
    return defaultSDF.format(Date(this))
}

fun String.formatDate(pattern: String = DEFAULT_PATTERN): String {
    defaultSDF.applyPattern(pattern)
    return defaultSDF.format(Date(toLong()))
}

fun String.deleteZero(pattern: String = "#0.########"): String {
    DECIMAL_FORMAT.applyPattern(pattern)
    return DECIMAL_FORMAT.format(toDouble())
}

fun Int.formatWeek(): String {
    return when (this) {
        1 -> {
            "星期日"
        }
        2 -> {
            "星期一"
        }
        3 -> {
            "星期二"
        }
        4 -> {
            "星期三"
        }
        5 -> {
            "星期四"
        }
        6 -> {
            "星期五"
        }
        7 -> {
            "星期六"
        }
        else -> {
            "未知"
        }
    }
}

fun String.isNumber(): Boolean {
    return try {
        toFloat()
        true
    } catch (e: Exception) {
        false
    }
}

fun String.toFinancialNumber(
    partitionsLength: Int = 3,
    partitionsMask: String = ",",
    currencyPrefix: String? = null,
): String {
    if (!isNumber()) {
        return this
    }
    //转换成浮点数进行操作,默认四舍五入
    val realStr = toDouble().formatDecimal(2)
    val splitIndex = realStr.indexOf('.')
    //小数部分
    val decimalStr = if (splitIndex < 0) "" else realStr.substring(splitIndex)
    //整数部分
    var integerOperateStr =
        (if (splitIndex < 0) realStr else realStr.substring(0, splitIndex)).reversed()
    //如果整数部分不能完整分割一次或者刚好分割一次,则直接返回
    if (integerOperateStr.length <= partitionsLength) {
        return this
    }
    //分割次数
    var partCount = integerOperateStr.length / partitionsLength
    //是否完整分割,如果是,减少一次分割
    if (integerOperateStr.length % partitionsLength == 0) {
        partCount--
    }
    //进行分割
    val sb = StringBuilder()
    while (partCount > 0) {
        sb.append(integerOperateStr.substring(0, partitionsLength)).append(partitionsMask)
        integerOperateStr = integerOperateStr.substring(partitionsLength)
        partCount--
    }
    //分割完成后,是否有剩余,如有,全部拼接上
    if (integerOperateStr.isNotEmpty()) {
        sb.append(integerOperateStr)
    }
    //拼接剩余部分
    if (!currencyPrefix.isNullOrEmpty()) {
        sb.append(currencyPrefix.reversed())
    }
    return sb.reverse().append(decimalStr).toString()
}

/**
 * 转换成大写
 */
fun String.capitalizationOfCurrency(): String {
    if (!isNumber()) {
        return this
    }
    //小数部分
    val decimalStr = if (indexOf('.') < 0) "" else substring(indexOf('.'))
    //整数部分
    val integerStr = if (indexOf('.') < 0) "" else substring(0, indexOf('.'))
    return ""
}

/**
 * 个位数前补零,如:
 *  1 -> 01
 *  9 -> 09
 */
fun Int.fillZeroWhenSingleDigit(): String {
    if (this < 10) {
        return String.format("0%1\$d", this)
    }
    return toString()
}

/**
 * 控制小数位,不足则补零,多余则直接截掉,如:传入小数精度为4
 * 1.0 -> 1.0000
 * 1.00000 -> 1.0000
 * 1.110111 -> 1.1101
 */
fun Double.formatDecimal(decimalPrecision: Int): String {
    return String.format("%.${decimalPrecision}f", this)
}

/**
 * 控制小数位,不足则补零,多余则直接截掉,如:传入小数精度为4
 * 1.0 -> 1.0000
 * 1.00000 -> 1.0000
 */
fun Float.formatDecimal(decimalPrecision: Int): String {
    return String.format("%.${decimalPrecision}f", this)
}

/**
 * 控制小数位,不足则补零,多余则直接截掉,如:传入小数精度为4
 * 1.0 -> 1.0000
 * 1.00000 -> 1.0000
 * 1.110111 -> 1.1101
 */
fun String.formatDecimal(decimalPrecision: Int): String {
    if (!isNumber()) {
        return this
    }
    return String.format("%.${decimalPrecision}f", toFloat())
}

fun Float.toStorageSize(precision: Int = 2): String {
    var tempSize = this
    var suffix = "B"
    if (tempSize > 1024) {
        suffix = "KB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "MB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "GB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "TB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "PB"
        tempSize /= 1024
    }
    return String.format("%1\$s%2\$s", String.format("%.${precision}f", tempSize), suffix)
}

fun Long.toStorageSize(precision: Int = 2): String {
    var tempSize = this.toFloat()
    var suffix = "B"
    if (tempSize > 1024) {
        suffix = "KB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "MB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "GB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "TB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "PB"
        tempSize /= 1024
    }
    return String.format("%1\$s%2\$s", String.format("%.${precision}f", tempSize), suffix)
}

fun Double.toStorageSize(precision: Int = 2): String {
    var tempSize = this.toFloat()
    var suffix = "B"
    if (tempSize > 1024) {
        suffix = "KB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "MB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "GB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "TB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "PB"
        tempSize /= 1024
    }
    return String.format("%1\$s%2\$s", String.format("%.${precision}f", tempSize), suffix)
}

fun Int.toStorageSize(precision: Int = 2): String {
    var tempSize = this.toFloat()
    var suffix = "B"
    if (tempSize > 1024) {
        suffix = "KB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "MB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "GB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "TB"
        tempSize /= 1024
    }
    if (tempSize > 1024) {
        suffix = "PB"
        tempSize /= 1024
    }
    return String.format("%1\$s%2\$s", String.format("%.${precision}f", tempSize), suffix)
}