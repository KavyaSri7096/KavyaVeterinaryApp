package com.example.kavyasynechrontask.ui

import android.os.Bundle
import com.example.kavyasynechrontaskapp.di.di
import com.example.kavyasynechrontaskapp.ui.MainPresenter


import com.example.kavyasynechrontask.R
import com.example.kavyasynechrontask.common.ui.PresenterActivity

class MainActivity : PresenterActivity<MainPresenter>() {

    private val presenter by lazy(LazyThreadSafetyMode.NONE) { obtainPresenter() }

    override fun presenterFactory(): MainPresenter = MainPresenter(di.navigation)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        withLifecycle(
            presenter.navigationObserver.observe { screen ->
                screen.show(R.id.container, supportFragmentManager)
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return false
    }
}