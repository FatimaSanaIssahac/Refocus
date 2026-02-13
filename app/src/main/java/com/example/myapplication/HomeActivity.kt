package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: HomePagerAdapter
    private lateinit var viewPager: ViewPager2

    // ----------------------------------
    // Activity Result for App Picker
    // ----------------------------------
    private val appPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {

                val appName = result.data?.getStringExtra("appName")
                val packageName = result.data?.getStringExtra("packageName")

                if (appName != null && packageName != null) {
                    showTimeInputDialog(appName, packageName)
                }
            }
        }

    // ----------------------------------
    // onCreate
    // ----------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Defer permission requests to avoid disrupting UI setup
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.postDelayed({
            requestUsageAccess()
            requestOverlayPermission()
            startMonitoringService()
        }, 500)

        viewPager = findViewById(R.id.viewPager)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)

        pagerAdapter = HomePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Limits" else "Boards"
        }.attach()

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                fab.text = if (position == 0) "Add Limit" else "Add Board"
            }
        })

        fab.setOnClickListener {
            if (viewPager.currentItem == 0) {
                val intent = Intent(this, AppSelectionActivity::class.java)
                appPickerLauncher.launch(intent)
            } else {
                startActivity(Intent(this, MoodboardActivity::class.java))
            }
        }
    }

    // ----------------------------------
    // Request Usage Permission
    // ----------------------------------
    private fun requestUsageAccess() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    // ----------------------------------
    // Request Overlay Permission
    // ----------------------------------
    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
        }
    }

    // ----------------------------------
    // Start Monitoring Service
    // ----------------------------------
    private fun startMonitoringService() {
        val intent = Intent(this, AppMonitorService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    // ----------------------------------
    // Time Input Dialog
    // ----------------------------------
    private fun showTimeInputDialog(appName: String, packageName: String) {

        val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
        builder.setTitle("Set time limit for $appName")

        val container = android.widget.LinearLayout(this)
        container.orientation = android.widget.LinearLayout.VERTICAL
        container.setPadding(50, 20, 50, 0)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Enter minutes"

        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("Set") { _, _ ->
            val minutesText = input.text.toString()

            if (minutesText.isNotEmpty()) {

                val minutes = minutesText.toInt()

                val newLimit = AppLimit(appName, packageName, minutes)
                pagerAdapter.limitsFragment.addNewLimit(newLimit)

                // Persist limits
                LimitsStorage.saveLimits(
                    this,
                    pagerAdapter.limitsFragment.getLimits()
                )
            }
        }

        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(R.color.primary))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(R.color.primary))

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
