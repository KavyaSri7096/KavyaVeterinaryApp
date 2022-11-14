package com.example.kavyasynechrontaskapp.domain.workinghours

import com.example.kavyasynechrontaskapp.domain.date.DateDomain
import com.example.kavyasynechrontaskapp.domain.date.TimeDomain
import com.example.kavyasynechrontaskapp.domain.workinghours.exceptions.InvalidCalendarProvidedException
import com.example.kavyasynechrontaskapp.testing.mockCalendar
import org.junit.Assert
import org.junit.Test
import java.util.*

class CurrentDateTest {

    private val expectedCurrentHour = DateDomain(2, TimeDomain(12, 15))

    @Test
    fun `test getting current hour with valid calendar`() {
        val calendar = mockCalendar(
            expectedCurrentHour.day,
            expectedCurrentHour.time.hour,
            expectedCurrentHour.time.minute
        )

        val currentDate = CurrentDate.Base(calendar)
        val actual = currentDate.get()

        Assert.assertEquals(actual, expectedCurrentHour)
    }

    @Test(expected = InvalidCalendarProvidedException::class)
    fun `should throw IllegalArgumentException if calendar's firstDayOfWeek is not Sunday`() {
        val calendar = mockCalendar(
            expectedCurrentHour.day,
            expectedCurrentHour.time.hour,
            expectedCurrentHour.time.minute,
            Locale.FRANCE
        )

        val currentTime = CurrentDate.Base(calendar)
    }
}