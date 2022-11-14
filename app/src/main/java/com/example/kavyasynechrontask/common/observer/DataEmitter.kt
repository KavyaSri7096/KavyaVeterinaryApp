package com.example.kavyasynechrontaskapp.common.observer

interface DataEmitter<T> {
    fun emit(data: T)
}