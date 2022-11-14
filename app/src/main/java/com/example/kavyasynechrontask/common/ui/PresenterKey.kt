package com.example.kavyasynechrontask.common.ui

interface PresenterKey {
    fun presenterKey(): String {
        return this::class.java.canonicalName!!
    }
}