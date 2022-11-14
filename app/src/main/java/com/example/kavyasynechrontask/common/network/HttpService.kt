package com.example.kavyasynechrontask.common.network

import com.example.kavyasynechrontask.common.network.MakeRequest
import okhttp3.OkHttpClient

abstract class HttpService<R>(client: OkHttpClient) : MakeRequest(client) {
    abstract fun get(onResult: (R) -> Unit, onError: (Throwable) -> Unit)
}