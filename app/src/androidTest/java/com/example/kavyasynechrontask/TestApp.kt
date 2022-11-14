package com.example.kavyasynechrontask

import com.example.kavyasynechrontask.di.DiContainer
import com.example.kavyasynechrontask.testing.mock.MockDiContainer
import com.example.kavyasynechrontask.App

class TestApp : App() {
    override val appContainer: DiContainer = MockDiContainer
}