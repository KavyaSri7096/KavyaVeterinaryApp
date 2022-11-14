package com.example.kavyasynechrontask.common.ui

import com.example.kavyasynechrontask.common.ui.WithLifecycle

class WithLifecycleCollector {
    private val children = mutableListOf<WithLifecycle>()

    operator fun WithLifecycle.unaryPlus(): WithLifecycle {
        this@WithLifecycleCollector.children.add(this)
        return this
    }

    fun build() = children
}