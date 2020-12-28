package com.cwand.lib.ktx.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : chunwei
 * @date : 2020/12/24
 * @description :格式化工具
 *
 */
class Formatter private constructor() {

    companion object {
        private const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss"
        private val DEFAULT_CALENDAR by lazy { Calendar.getInstance() }

        @SuppressLint("SimpleDateFormat")
        private var defaultSDF = SimpleDateFormat(DEFAULT_PATTERN)

        @SuppressLint("SimpleDateFormat")
        fun updateLocale() {
            defaultSDF = SimpleDateFormat(DEFAULT_PATTERN)
        }

        fun formatStorageSize(size: Long, precision: Int = 2): String {
            var tempSize = size
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

        fun formatDate(dateTimestamp: Long, pattern: String = DEFAULT_PATTERN): String {
            defaultSDF.applyPattern(pattern)
            return defaultSDF.format(Date(dateTimestamp))
        }

        fun getCurrentYear(): Int {
            return DEFAULT_CALENDAR[Calendar.YEAR]
        }

        fun getCurrentMonth(): Int {
            return DEFAULT_CALENDAR[Calendar.MONTH]
        }

        fun getCurrentMonthFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentMonth())
        }

        fun getCurrentDayOfWeek(): Int {
            return DEFAULT_CALENDAR[Calendar.DAY_OF_WEEK]
        }

        fun getCurrentDayOfWeekFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentDayOfWeek())
        }

        fun getCurrentDayOfWeekInMonth(): Int {
            return DEFAULT_CALENDAR[Calendar.DAY_OF_WEEK_IN_MONTH]
        }

        fun getCurrentDayOfWeekInMonthFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentDayOfWeekInMonth())
        }

        fun getCurrentDayOfMonth(): Int {
            return DEFAULT_CALENDAR[Calendar.DAY_OF_MONTH]
        }

        fun getCurrentDayOfMonthFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentDayOfMonth())
        }

        fun getCurrentDayOfYear(): Int {
            return DEFAULT_CALENDAR[Calendar.DAY_OF_YEAR]
        }

        fun getCurrentDayOfYearFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentDayOfYear())
        }

        fun getCurrentWeekOfMonth(): Int {
            return DEFAULT_CALENDAR[Calendar.WEEK_OF_MONTH]
        }

        fun getCurrentWeekOfMonthFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentWeekOfMonth())
        }

        fun getCurrentWeekOfYear(): Int {
            return DEFAULT_CALENDAR[Calendar.WEEK_OF_YEAR]
        }

        fun getCurrentWeekOfYearFillZero(): String {
            return fillZeroWhenSingleDigit(getCurrentWeekOfYear())
        }

        fun fillZeroWhenSingleDigit(value: Int): String {
            if (value < 10) {
                return String.format("0%1\$d", value)
            }
            return value.toString()
        }

        fun formatWeek(week: Int): String {
            return when (week) {
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

        fun getFirstDayOfWeekInMonth(
            year: Int = getCurrentYear(),
            month: Int = getCurrentMonth() - 1,
        ): Int {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = 1
            return calendar[Calendar.DAY_OF_WEEK]
        }

        fun formatFirstDayOfWeekInMonth(): String {
            return formatWeek(getFirstDayOfWeekInMonth())
        }

        fun getCurrentMonthDays(
            year: Int = getCurrentYear(),
            month: Int = getCurrentMonth() - 1,
        ): Int {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DATE] = 1
            calendar.roll(Calendar.DATE, -1)
            return calendar[Calendar.DATE]
        }
    }

}



