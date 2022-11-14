package com.example.kavyasynechrontask.data.cloud

import com.example.kavyasynechrontask.common.network.HttpService
import com.example.kavyasynechrontask.common.network.UnmarshallResponse
import okhttp3.OkHttpClient


class ConfigHttpService(
    private val url: String,
    private val unmarshallResponse: UnmarshallResponse<ConfigCloud>,
    client: OkHttpClient
) : HttpService<ConfigCloud>(client) {

    override fun get(
        onResult: (ConfigCloud) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        makeRequest(url, { response ->
            try {
                onResult.invoke(unmarshallResponse.unmarshall(response))
            } catch (e: Exception) {
                onError.invoke(e)
            }
        }, onError)
    }
}