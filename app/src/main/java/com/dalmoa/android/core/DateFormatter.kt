package com.dalmoa.android.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.dalmoa.android.model.Term
import java.util.Calendar

val WEEKDAY_NAMES = listOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

// Calendar.DAY_OF_WEEK(일=1~토=7)을 앱 전역에서 쓰는 요일 인덱스(월=1~일=7)로 변환
private fun toAppWeekday(calendarDayOfWeek: Int): Int =
    if (calendarDayOfWeek == Calendar.SUNDAY) 7 else calendarDayOfWeek - 1

private fun daysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, 1)
    }
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

// 월간: 구독 시작일(연/월) + 매월 결제일(일)을 실제 달력 날짜로 인코딩
fun encodeMonthDate(year: Int, month: Int, day: Int): String {
    val clampedDay = day.coerceAtMost(daysInMonth(year, month))
    return "%04d-%02d-%02d".format(year, month, clampedDay)
}

// 연간: 구독 시작일(연) + 매년 결제 월/일을 실제 달력 날짜로 인코딩
fun encodeYearDate(year: Int, month: Int, day: Int): String {
    val clampedDay = day.coerceAtMost(daysInMonth(year, month))
    return "%04d-%02d-%02d".format(year, month, clampedDay)
}

// 주간: 구독 시작일(연/월) 이후 날짜 중, 원하는 요일(1=월~7=일)과 실제로 일치하는 첫 날짜를 찾아 인코딩
fun encodeWeekDate(year: Int, month: Int, day: Int, weekday: Int): String {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, day.coerceAtMost(daysInMonth(year, month)))
    }
    val currentWeekday = toAppWeekday(cal.get(Calendar.DAY_OF_WEEK))
    val shift = (weekday - currentWeekday + 7) % 7
    cal.add(Calendar.DAY_OF_MONTH, shift)
    return "%04d-%02d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
}

private fun parseDateParts(dateStr: String): Triple<Int, Int, Int> {
    val parts = dateStr.substringBefore("T").split("-")
    return Triple(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
}

fun decodeStartYear(dateStr: String): Int = parseDateParts(dateStr).first
fun decodeStartMonth(dateStr: String): Int = parseDateParts(dateStr).second
fun decodeStartDay(dateStr: String): Int = parseDateParts(dateStr).third

fun decodeWeekday(dateStr: String): Int {
    val (y, m, d) = parseDateParts(dateStr)
    val cal = Calendar.getInstance().apply { clear(); set(y, m - 1, d) }
    return toAppWeekday(cal.get(Calendar.DAY_OF_WEEK))
}
fun decodeYearMonth(dateStr: String): Int = parseDateParts(dateStr).second
fun decodeYearDay(dateStr: String): Int = parseDateParts(dateStr).third
fun decodeMonthDay(dateStr: String): Int = parseDateParts(dateStr).third

// "20261010" 같은 숫자 입력을 "2026.10.10" 형식으로 자동 포맷하고,
// 8자리가 모두 입력되면 onDateChanged로 연/월/일을 전달 (달력 아이콘 탭 시 직접 setText할 때도 재사용됨)
fun attachDateInputFormatter(editText: EditText, onDateChanged: (year: Int, month: Int, day: Int) -> Unit) {
    editText.addTextChangedListener(object : TextWatcher {
        private var isFormatting = false
        private var lastDigits = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isFormatting) return

            val digits = s.toString().filter { it.isDigit() }.take(8)
            if (digits == lastDigits) return
            lastDigits = digits

            val formatted = buildString {
                digits.forEachIndexed { i, c ->
                    append(c)
                    if (i == 3 || i == 5) append(".")
                }
            }

            isFormatting = true
            editText.setText(formatted)
            editText.setSelection(formatted.length)
            isFormatting = false

            if (digits.length == 8) {
                val year = digits.substring(0, 4).toInt()
                val month = digits.substring(4, 6).toInt()
                val day = digits.substring(6, 8).toInt()
                if (month in 1..12 && day in 1..31) {
                    onDateChanged(year, month, day)
                }
            }
        }
    })
}

fun formatDate(dateStr: String, term: Term): String {
    return try {
        val (_, month, day) = parseDateParts(dateStr)
        when (term) {
            Term.WEEK -> "매주 ${WEEKDAY_NAMES[decodeWeekday(dateStr) - 1]}"
            Term.YEAR -> "매년 ${month}월 ${day}일"
            Term.MONTH -> "매월 ${day}일"
        }
    } catch (e: Exception) {
        dateStr.substringBefore("T")
    }
}
