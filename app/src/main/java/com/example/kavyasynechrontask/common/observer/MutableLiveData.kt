package com.example.kavyasynechrontaskapp.common.observer

interface MutableLiveData<T> : DataObserver<T>, DataEmitter<T> {
    fun asDataObserver(): DataObserver<T> = this
}