package com.khudyakovvladimir.vhcloudnotepad.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TimeHelper @Inject constructor(){

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {

        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone)
        val simpleDateFormatHoursAndMinutes = SimpleDateFormat("HH:mm")
        val simpleDateFormatDay = SimpleDateFormat("dd")
        val simpleDateFormatMonth = SimpleDateFormat("MM")
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

        var day = simpleDateFormatDay.format(calendar.time)
        val month = simpleDateFormatMonth.format(calendar.time)
        val hoursAndMinutes = simpleDateFormatHoursAndMinutes.format(calendar.time)

        if(day[0] == '0') {
            day = day[1].toString()
        }

        var monthAsWord = getMonth(month)

        return "$day $monthAsWord - $hoursAndMinutes"
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeForNotification(date: Date): String {

        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone)
        calendar.timeZone = timeZone

        val simpleDateFormatHoursAndMinutes = SimpleDateFormat("HH:mm")
        val simpleDateFormatDay = SimpleDateFormat("dd")
        val simpleDateFormatMonth = SimpleDateFormat("MM")
        val simpleDateFormatYear = SimpleDateFormat("yyyy")
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

        var day = simpleDateFormatDay.format(date)
        val month = simpleDateFormatMonth.format(date)
        val year = simpleDateFormatYear.format(date)
        val hoursAndMinutes = simpleDateFormatHoursAndMinutes.format(date)

        if(day[0] == '0') {
            day = day[1].toString()
        }

        var monthAsWord = getMonth(month)

        return "$day $monthAsWord  $year - $hoursAndMinutes"
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeForTextView(): String {

        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone)
        val simpleDateFormatHoursAndMinutes = SimpleDateFormat("HH:mm:ss")
        val simpleDateFormatDay = SimpleDateFormat("dd")
        val simpleDateFormatMonth = SimpleDateFormat("MM")
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

        var day = simpleDateFormatDay.format(calendar.time)
        val month = simpleDateFormatMonth.format(calendar.time)
        val hoursAndMinutes = simpleDateFormatHoursAndMinutes.format(calendar.time)

        if(day[0] == '0') {
            day = day[1].toString()
        }

        val monthAsWord = getMonth(month)

        return "$day $monthAsWord   $hoursAndMinutes"
    }

    private fun getMonth(month: String): String {
        var monthAsWord = ""
        when (month) {
            "01" -> {
                monthAsWord = "января"
            }
            "02" -> {
                monthAsWord = "февраля"
            }
            "03" -> {
                monthAsWord = "марта"
            }
            "04" -> {
                monthAsWord = "апреля"
            }
            "05" -> {
                monthAsWord = "мая"
            }
            "06" -> {
                monthAsWord = "июня"
            }
            "07" -> {
                monthAsWord = "июля"
            }
            "08" -> {
                monthAsWord = "августа"
            }
            "09" -> {
                monthAsWord = "сентября"
            }
            "10" -> {
                monthAsWord = "октября"
            }
            "11" -> {
                monthAsWord = "ноября"
            }
            "12" -> {
                monthAsWord = "декабря"
            }
        }
        return monthAsWord
    }
}