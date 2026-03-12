package com.labinot.bajrami.nutritionapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.labinot.bajrami.nutritionapp.data.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin (
            config = {

                androidContext(this@MyApplication)
            }
        )
        Firebase.initialize(context = this)
    }

}