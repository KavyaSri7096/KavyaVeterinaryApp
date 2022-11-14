package com.example.kavyasynechrontaskapp.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.kavyasynechrontask.App

val Activity.di: DiContainer get() = (application as App).appContainer
val Fragment.di: DiContainer get() = requireActivity().di