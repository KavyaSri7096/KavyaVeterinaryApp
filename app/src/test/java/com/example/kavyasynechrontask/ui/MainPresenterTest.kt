package com.example.kavyasynechrontask.ui

import com.example.kavyasynechrontask.common.ui.UiExecutor
import com.example.kavyasynechrontask.navigation.Navigation
import com.example.kavyasynechrontask.navigation.Screen
import com.example.kavyasynechrontask.testing.getOrAwaitValue
import com.example.kavyasynechrontask.ui.MainPresenter
import org.junit.Assert
import org.junit.Test

class MainPresenterTest {

    @Test
    fun testScreenNavigation() {
        val navigation = Navigation.Base(UiExecutor.Test())
        val presenter = MainPresenter(navigation)
        val expectedScreen = Screen.Test

        navigation.to(expectedScreen)

        Assert.assertEquals(expectedScreen, presenter.navigationObserver.getOrAwaitValue())
    }
}