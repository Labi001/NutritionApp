package com.labinot.bajrami.nutritionapp.domain.helpers.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import dev.gitlive.firebase.storage.File


@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class PhotoPicker {

    private var openPhotoPicker = mutableStateOf(false)

    actual fun open() {
        openPhotoPicker.value = true
    }

    @Composable
    actual fun InitializePhotoPicker(onImageSelect: (ByteArray?) -> Unit) {
    }
}

