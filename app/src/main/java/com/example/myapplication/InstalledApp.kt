package com.example.myapplication

import android.graphics.drawable.Drawable

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Drawable
)
