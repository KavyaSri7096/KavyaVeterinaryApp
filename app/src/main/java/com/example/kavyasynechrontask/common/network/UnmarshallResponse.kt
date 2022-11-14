package com.example.kavyasynechrontask.common.network

import okhttp3.Response

interface UnmarshallResponse<R> {
    fun unmarshall(response: Response): R
}