package com.dalmoa.android.core

import com.dalmoa.android.model.Term

val WEEKDAY_NAMES = listOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

// 주간/연간은 연·월(혹은 연) 정보가 의미가 없으므로, 계산에 필요한 값(요일 / 월,일)만
// 고정된 기준 날짜에 실어 서버로 보낸다. 2024-01-01은 월요일이라 day(1~7)가 곧 요일이 된다.
fun encodeWeekDate(weekday: Int): String = String.format("2024-01-%02d", weekday)
fun encodeYearDate(month: Int, day: Int): String = String.format("2000-%02d-%02d", month, day)
fun encodeMonthDate(day: Int): String = String.format("2000-01-%02d", day)

private fun parseMonthDay(dateStr: String): Pair<Int, Int> {
    val parts = dateStr.substringBefore("T").split("-")
    return parts[1].toInt() to parts[2].toInt()
}

fun decodeWeekday(dateStr: String): Int = parseMonthDay(dateStr).second
fun decodeYearMonth(dateStr: String): Int = parseMonthDay(dateStr).first
fun decodeYearDay(dateStr: String): Int = parseMonthDay(dateStr).second
fun decodeMonthDay(dateStr: String): Int = parseMonthDay(dateStr).second

fun formatDate(dateStr: String, term: Term): String {
    return try {
        val (month, day) = parseMonthDay(dateStr)
        when (term) {
            Term.WEEK -> "매주 ${WEEKDAY_NAMES[day - 1]}"
            Term.YEAR -> "매년 ${month}월 ${day}일"
            Term.MONTH -> "매월 ${day}일"
        }
    } catch (e: Exception) {
        dateStr.substringBefore("T")
    }
}
