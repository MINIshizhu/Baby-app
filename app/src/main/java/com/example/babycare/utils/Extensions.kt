package com.example.babycare.utils

import java.text.SimpleDateFormat
import java.util.*

// 扩展函数
fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toTimeString(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toDurationString(): String {
    val hours = this / (60 * 60 * 1000)
    val minutes = (this % (60 * 60 * 1000)) / (60 * 1000)
    return String.format("%d小时%d分钟", hours, minutes)
} 