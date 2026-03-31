package com.example.save_a_strayv2

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SaveAStrayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        val config = mapOf("cloud_name" to "YOUR_CLOUD_NAME")
        MediaManager.init(this, config)
    }
}
