package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BlockingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This activity is no longer used.
        // Immediately close to prevent accidental launches.
        finish()
    }
}
