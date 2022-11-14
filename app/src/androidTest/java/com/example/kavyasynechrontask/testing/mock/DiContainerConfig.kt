package com.example.kavyasynechrontask.testing.mock

import com.example.kavyasynechrontask.domain.ConfigDomain
import com.example.kavyasynechrontask.domain.PetDomain
import com.example.kavyasynechrontask.domain.date.DateRangeDomain
import com.example.kavyasynechrontask.domain.date.DayDomain
import com.example.kavyasynechrontask.domain.date.TimeDomain
import com.example.kavyasynechrontask.domain.date.WorkHoursDomain

data class DiContainerConfig(
    val mockedConfig: ConfigDomain = ConfigDomain(
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
    ),
    val mockedPetList: List<PetDomain> = listOf(
        PetDomain(
            imageUrl = "image",
            title = "pet",
            contentUrl = "https://google.com"
        )
    ),
    val isConfigError: Boolean = false,
    val isPetListError: Boolean = false,
    val isWorkingHours: Boolean = true
)
