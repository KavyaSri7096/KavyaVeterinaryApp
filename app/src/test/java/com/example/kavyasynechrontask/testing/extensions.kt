package com.example.kavyasynechrontaskapp.testing

import com.example.kavyasynechrontaskapp.common.observer.DataObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> DataObserver<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    this.observe {
        data = it
        latch.countDown()
    }

    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("DataObserver value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}