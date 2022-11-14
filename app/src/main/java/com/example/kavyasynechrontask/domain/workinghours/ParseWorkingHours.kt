package com.example.kavyasynechrontask.domain.workinghours


import com.example.kavyasynechrontaskapp.domain.date.DateRangeDomain
import com.example.kavyasynechrontaskapp.domain.date.DayDomain
import com.example.kavyasynechrontaskapp.domain.date.TimeDomain
import com.example.kavyasynechrontaskapp.domain.date.WorkHoursDomain
import com.example.kavyasynechrontaskapp.domain.workinghours.exceptions.WorkHoursInvalidFormatException
import com.example.kavyasynechrontaskapp.domain.workinghours.exceptions.WorkHoursInvalidParseException

interface ParseWorkingHours {
    fun parse(s: String): WorkHoursDomain

    class Base(private val weekDays: Map<String, Int> = LOCALE_US_WEEK) : ParseWorkingHours {
        override fun parse(s: String): WorkHoursDomain {
            try {
                val (dayRange, fromHours, _, toHours) = s.split(' ')
                val (fromHour, fromMinute) = fromHours.split(':')
                val (toHour, toMinute) = toHours.split(':')
                val (fromDay, toDay) = dayRange.split('-')

                return WorkHoursDomain(
                    origin = s,
                    dateRange = DateRangeDomain(
                        weekDays[fromDay] ?: throw WorkHoursInvalidFormatException(),
                        weekDays[toDay] ?: throw WorkHoursInvalidFormatException(),
                        TimeDomain(fromHour.toInt(), fromMinute.toInt()),
                        TimeDomain(toHour.toInt(), toMinute.toInt())
                    )
                )
            } catch (e: Exception) {
                throw WorkHoursInvalidParseException(e)
            }
        }

        companion object {
            private val LOCALE_US_WEEK = mapOf(
                "SU" to DayDomain.SU,
                "M" to DayDomain.M,
                "TU" to DayDomain.TU,
                "W" to DayDomain.W,
                "TH" to DayDomain.TH,
                "F" to DayDomain.F,
                "SA" to DayDomain.SA
            )
        }
    }
}