package com.example.kavyasynechrontask.ui.list


import com.example.kavyasynechrontask.data.cloud.ConfigCloud

import com.example.kavyasynechrontask.domain.workinghours.ParseWorkingHours
import com.example.kavyasynechrontaskapp.common.ui.Mapper
import com.example.kavyasynechrontaskapp.domain.ConfigDomain

class ConfigMapper(
    private val parseWorkingHours: ParseWorkingHours
) : Mapper<ConfigCloud, ConfigDomain> {
    override fun map(data: ConfigCloud): ConfigDomain {
        return ConfigDomain(
            data.isChatEnabled,
            data.isCallEnabled,
            parseWorkingHours.parse(data.workHours)
        )
    }
}