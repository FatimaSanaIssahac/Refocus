package com.example.myapplication

import android.content.Intent
import android.os.Bundle
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

    // Modern Activity Result API (replaces onActivityResult)
    private val appPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val appName = result.data?.getStringExtra("appName")
                if (appName != null) {
                    showTimeInputDialog(appName)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)

        pagerAdapter = HomePagerAdapter(this)
        viewPager.adapter = pagerAdapter


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Limits" else "Boards"
        }.attach()

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    fab.text = "Add Limit"
                } else {
                    fab.text = "Add Board"
                }
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

    private fun showTimeInputDialog(appName: String) {

        val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
        builder.setTitle("Set time limit for $appName")

        // Create container layout with padding
        val container = android.widget.LinearLayout(this)
        container.orientation = android.widget.LinearLayout.VERTICAL
        container.setPadding(50, 20, 50, 0) // left, top, right, bottom

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Enter minutes"

        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("Set") { _, _ ->
            val minutes = input.text.toString()
            if (minutes.isNotEmpty()) {
                pagerAdapter.limitsFragment.addNewLimit(
                    AppLimit(appName, "$minutes minutes")
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
