package com.labinot.bajrami.nutritionapp.data

import com.labinot.bajrami.nutritionapp.domain.helpers.utils.PhotoPicker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule = module {

    single<PhotoPicker> { PhotoPicker() }

}