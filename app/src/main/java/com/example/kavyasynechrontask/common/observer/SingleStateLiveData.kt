package com.example.kavyasynechrontaskapp.common.observer

import com.example.kavyasynechrontaskapp.common.ui.UiExecutor
import com.example.kavyasynechrontask.common.ui.WithLifecycle

class SingleStateLiveData<T>(
    initial: T,
    private val uiExecutor: UiExecutor
) : MutableLiveData<T> {

    private var data: T = initial
    private var observer: ((T) -> Unit)? = null

    val value get() = data

    override fun observe(observer: (T) -> Unit): WithLifecycle {
        this.observer = observer
        uiExecutor.execute { observer.invoke(data) }
        return this
    }

    override fun emit(data: T) {
        this.data = data
        observer?.let { uiExecutor.execute { it.invoke(data) } }
    }

    override fun removeObserver() {
        this.observer = null
    }

    override fun onDestroy() {
        removeObserver()
    }
}