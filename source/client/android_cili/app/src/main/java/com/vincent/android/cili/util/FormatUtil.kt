package com.vincent.android.cili.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * 格式化工具类
 */
object FormatUtil {

    /**
     * 数字转万
     * @param count 数字
     * @return 格式化后的字符串
     */
    fun countFormat(count: Long): String {
        return if (count > 9999) {
            if (count % 10000 == 0L) {
                "%d万".format(count / 10000)
            } else {
                "%.2f万".format(count / 10000.0)
            }
        } else {
            count.toString()
        }
    }

    fun countFormatNoFloat(count: Int): String {
        return if (count > 9999) {
            "%d万".format(count / 10000)
        } else {
            count.toString()
        }
    }

    fun countFormat(count: Int): String {
        return if (count > 9999) {
            "%.2f万".format(count / 10000.0)
        } else {
            count.toString()
        }
    }

    /**
     * 时长格式化
     * @param seconds 秒数
     * @return 格式化后的时长字符串 (m:ss)
     */
    fun durationTransform(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return if (remainingSeconds < 10) {
            "$minutes:0$remainingSeconds"
        } else {
            "$minutes:$remainingSeconds"
        }
    }

    /**
     * 日期格式化，获取月-日格式
     * @param dateStr 日期字符串 (格式: 2022-06-11 20:06:43)
     * @return 格式化后的日期字符串 (MM-dd)
     */
    fun dateMonthAndDay(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: getCurrentMonthDay()
        } catch (e: Exception) {
            // 如果解析失败，返回当前月-日
            getCurrentMonthDay()
        }
    }

    /**
     * 获取当前月-日
     * @return 当前月-日字符串
     */
    private fun getCurrentMonthDay(): String {
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * 获取相对时间描述
     * @param dateStr 日期字符串
     * @return 相对时间描述 (如: 刚刚、5分钟前、1小时前、昨天等)
     */
    fun getRelativeTime(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { getRelativeTime(it.time) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取相对时间描述
     * @param timestamp 时间戳
     * @return 相对时间描述
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 2 * 24 * 60 * 60 * 1000 -> "昨天"
            diff < 3 * 24 * 60 * 60 * 1000 -> "前天"
            else -> {
                val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }

    /**
     * 时间戳转日期字符串
     * @param timestamp 时间戳（毫秒）
     * @param pattern 日期格式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    fun timestampToDate(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 时间戳转日期字符串（秒级时间戳）
     * @param timestamp 时间戳（秒）
     * @param pattern 日期格式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    fun timestampSecondsToDate(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return timestampToDate(timestamp * 1000, pattern)
    }

    /**
     * 时间戳转相对时间（秒级时间戳）
     * @param timestamp 时间戳（秒）
     * @return 相对时间描述
     */
    fun timestampSecondsToRelativeTime(timestamp: Long): String {
        return getRelativeTime(timestamp * 1000)
    }

    /**
     * 时间戳转月-日格式
     * @param timestamp 时间戳（毫秒）
     * @return 月-日格式字符串
     */
    fun timestampToMonthDay(timestamp: Long): String {
        return timestampToDate(timestamp, "MM-dd")
    }

    /**
     * 时间戳转月-日格式（秒级时间戳）
     * @param timestamp 时间戳（秒）
     * @return 月-日格式字符串
     */
    fun timestampSecondsToMonthDay(timestamp: Long): String {
        return timestampToMonthDay(timestamp * 1000)
    }

    /**
     * 获取当前时间戳（毫秒）
     * @return 当前时间戳
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 获取当前时间戳（秒）
     * @return 当前时间戳（秒）
     */
    fun getCurrentTimestampSeconds(): Long {
        return System.currentTimeMillis() / 1000
    }

    /**
     * 日期字符串转时间戳
     * @param dateStr 日期字符串
     * @param pattern 日期格式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 时间戳（毫秒），解析失败返回0
     */
    fun dateToTimestamp(dateStr: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
        return try {
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            val date = dateFormat.parse(dateStr)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 日期字符串转时间戳（秒）
     * @param dateStr 日期字符串
     * @param pattern 日期格式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 时间戳（秒），解析失败返回0
     */
    fun dateToTimestampSeconds(dateStr: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
        return dateToTimestamp(dateStr, pattern) / 1000
    }
} 