package com.example.kavyasynechrontaskapp.ui.list

import com.example.kavyasynechrontaskapp.common.ui.Mapper
import com.example.kavyasynechrontaskapp.data.cloud.PetCloud
import com.example.kavyasynechrontaskapp.domain.PetDomain

class PetListMapper : Mapper<List<PetCloud>, List<PetDomain>> {
    override fun map(data: List<PetCloud>): List<PetDomain> {
        return data.map { PetDomain(it.imageUrl, it.title, it.contentUrl) }
    }
}