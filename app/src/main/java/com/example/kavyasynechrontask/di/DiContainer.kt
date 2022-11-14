package com.example.kavyasynechrontaskapp.di

import com.example.kavyasynechrontask.common.fetchimage.FetchImage
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontaskapp.domain.ConfigDomain
import com.example.kavyasynechrontaskapp.domain.PetDomain
import com.example.kavyasynechrontaskapp.domain.workinghours.CheckWorkHours
import com.example.kavyasynechrontaskapp.navigation.Navigation

interface DiContainer {
    val navigation: Navigation.Component
    val fetchConfigUseCase: UseCase<ConfigDomain>
    val fetchPetsUseCase: UseCase<List<PetDomain>>
    val checkWorkHours: CheckWorkHours
    val fetchImage: FetchImage
}