package com.example.kavyasynechrontaskapp.common.observer

import com.example.kavyasynechrontask.common.ui.WithLifecycle

interface DataObserver<T> : WithLifecycle {
    fun observe(observer: (T) -> Unit): WithLifecycle
    fun removeObserver()
}