package com.example.kavyasynechrontaskapp.domain.date

data class DateDomain(
    @DayDomain val day: Int,
    val time: TimeDomain
)