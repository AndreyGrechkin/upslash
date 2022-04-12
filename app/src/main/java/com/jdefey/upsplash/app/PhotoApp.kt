package com.jdefey.upsplash.app

import android.app.Application
import com.jdefey.upsplash.database.Database
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PhotoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.init(this)
    }
}
