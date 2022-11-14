package com.example.kavyasynechrontaskapp.data.cloud

import com.example.kavyasynechrontask.common.network.HttpService
import com.example.kavyasynechrontask.common.network.UnmarshallResponse
import okhttp3.OkHttpClient


class PetHttpService(
    private val url: String,
    private val unmarshallResponse: UnmarshallResponse<List<PetCloud>>,
    client: OkHttpClient
) : HttpService<List<PetCloud>>(client) {

    override fun get(
        onResult: (List<PetCloud>) -> Unit,
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