package com.example.kavyasynechrontask.ui.list

import com.example.kavyasynechrontask.data.cloud.ConfigCloud
import com.example.kavyasynechrontask.domain.ConfigDomain
import com.example.kavyasynechrontask.domain.date.DateRangeDomain
import com.example.kavyasynechrontask.domain.date.DayDomain
import com.example.kavyasynechrontask.domain.date.TimeDomain
import com.example.kavyasynechrontask.domain.date.WorkHoursDomain
import com.example.kavyasynechrontask.domain.workinghours.ParseWorkingHours
import org.junit.Assert
import org.junit.Test

class ConfigMapperTest {

    private val configMapper = ConfigMapper(ParseWorkingHours.Base())

    @Test
    fun testConfigMapper() {
        val data = ConfigCloud(
            isChatEnabled = true,
            isCallEnabled = true,
            workHours = "M-F 9:00 - 18:00"
        )

        val expected = ConfigDomain(
            isChatEnabled = true,
            isCallEnabled = true,
            workingHours = WorkHoursDomain(
                "M-F 9:00 - 18:00",
                DateRangeDomain(
                    DayDomain.M, DayDomain.F,
                    TimeDomain(9, 0),
                    TimeDomain(18, 0)
                )
            )
        )

        val actual = configMapper.map(data)

        Assert.assertEquals(expected, actual)
    }
}