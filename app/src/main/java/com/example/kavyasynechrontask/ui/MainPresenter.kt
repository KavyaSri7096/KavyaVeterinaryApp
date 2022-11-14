package com.example.kavyasynechrontaskapp.ui

import com.example.kavyasynechrontask.common.ui.Presenter
import com.example.kavyasynechrontaskapp.navigation.Navigation

class MainPresenter(private val navigation: Navigation.Observe) : Presenter {
    val navigationObserver get() = navigation.observe()
}