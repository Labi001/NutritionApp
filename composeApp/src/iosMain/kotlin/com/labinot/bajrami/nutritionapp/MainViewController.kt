package com.labinot.bajrami.nutritionapp

import androidx.compose.ui.window.ComposeUIViewController
import com.labinot.bajrami.nutritionapp.data.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }