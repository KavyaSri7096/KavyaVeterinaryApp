package com.example.kavyasynechrontask

import android.app.Application

import com.example.kavyasynechrontask.di.AppContainer
import com.example.kavyasynechrontaskapp.di.DiContainer
import timber.log.Timber

open class App : Application() {

    open val appContainer: DiContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}