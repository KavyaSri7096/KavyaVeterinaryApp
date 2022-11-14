package com.example.kavyasynechrontaskapp.common.ui

interface Mapper<I, O> {
    fun map(data: I): O
}