package com.example.taxflow.util

import kotlinx.datetime.LocalDate

fun formatDate(date: LocalDate): String =
    "%02d.%02d.%04d".format(date.dayOfMonth, date.monthNumber, date.year)