package com.example.kavyasynechrontaskapp.navigation

import com.example.kavyasynechrontaskapp.common.observer.DataObserver
import com.example.kavyasynechrontaskapp.common.observer.SingleEventLiveData
import com.example.kavyasynechrontaskapp.common.ui.UiExecutor

interface Navigation {

    interface Route {
        fun to(screen: Screen)
    }

    interface Observe {
        fun observe(): DataObserver<Screen>
    }

    interface Component : Route, Observe

    class Base(uiExecutor: UiExecutor = UiExecutor.Main()) : Component {

        private val liveData: SingleEventLiveData<Screen> = SingleEventLiveData(uiExecutor)

        override fun to(screen: Screen) {
            liveData.emit(screen)
        }

        override fun observe(): DataObserver<Screen> =
            liveData.asDataObserver()
    }
}