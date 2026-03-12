package com.labinot.bajrami.nutritionapp.domain.helpers.utils

import platform.posix.usleep

actual fun threadSleep(millis: Long) {
    usleep((millis * 1000).toUInt())
}