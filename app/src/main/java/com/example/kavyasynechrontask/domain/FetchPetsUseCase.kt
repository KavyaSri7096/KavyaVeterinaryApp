package com.example.kavyasynechrontaskapp.domain

import com.example.kavyasynechrontask.common.network.HttpService
import com.example.kavyasynechrontaskapp.common.ui.Mapper
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontaskapp.data.cloud.PetCloud

class FetchPetsUseCase(
    private val petService: HttpService<List<PetCloud>>,
    private val mapper: Mapper<List<PetCloud>, List<PetDomain>>
) : UseCase<List<PetDomain>> {

    override fun invoke(onResult: (List<PetDomain>) -> Unit, onError: (Throwable) -> Unit) {
        try {
            petService.get({ onResult.invoke(mapper.map(it)) }, onError)
        } catch (e: Exception) {
            onError.invoke(e)
        }
    }
}