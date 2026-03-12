package com.labinot.bajrami.nutritionapp.domain.helpers.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.gitlive.firebase.storage.File

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class PhotoPicker {

    private var openPhotoPicker = mutableStateOf(false)

    actual fun open() {
        openPhotoPicker.value = true
    }

    @Composable
    actual fun InitializePhotoPicker(onImageSelect: (ByteArray?) -> Unit) { // Changed from File? to ByteArray?
        val context = LocalContext.current
        val openPhotoPickerState by openPhotoPicker

        val pickMedia = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                // Convert Uri to ByteArray right here
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                onImageSelect(bytes)
            } else {
                onImageSelect(null)
            }
            openPhotoPicker.value = false
        }

        LaunchedEffect(openPhotoPickerState) {
            if (openPhotoPickerState) {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }


}