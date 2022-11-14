package com.example.kavyasynechrontaskapp.domain

import com.example.kavyasynechrontask.common.network.HttpService
import com.example.kavyasynechrontask.common.ui.UseCase
import com.example.kavyasynechrontask.data.cloud.ConfigCloud
import com.example.kavyasynechrontask.ui.list.ConfigMapper


class FetchConfigUseCase(
    private val configService: HttpService<ConfigCloud>,
    private val mapper: ConfigMapper
) : UseCase<ConfigDomain> {

    override fun invoke(onResult: (ConfigDomain) -> Unit, onError: (Throwable) -> Unit) {
        try {
            configService.get({ onResult.invoke(mapper.map(it)) }, onError)
        } catch (e: Exception) {
            onError.invoke(e)
        }
    }
}