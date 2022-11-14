package com.example.kavyasynechrontask.common.async

typealias Callback<V> = (onResult: (V) -> Unit, onError: (Throwable) -> Unit) -> Unit