package com.example.kavyasynechrontask.navigation

import com.example.kavyasynechrontask.common.ui.UiExecutor
import com.example.kavyasynechrontask.testing.getOrAwaitValue
import org.junit.Assert
import org.junit.Test

class NavigationTest {
    private val navigation = Navigation.Base(UiExecutor.Test())

    @Test
    fun testNavigation() {
        val expectedScreen = Screen.Test
        navigation.to(expectedScreen)
        Assert.assertEquals(expectedScreen, navigation.observe().getOrAwaitValue())
    }
}