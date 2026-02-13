package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityAppSelectionBinding

class AppSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSelectionBinding
    private val appList = mutableListOf<InstalledApp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadInstalledApps()

        binding.recyclerApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerApps.adapter =
            InstalledAppAdapter(appList) { selectedApp ->

                val resultIntent = Intent()
                resultIntent.putExtra("appName", selectedApp.appName)
                resultIntent.putExtra("packageName", selectedApp.packageName)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
    }

    private fun loadInstalledApps() {

        Thread {
            val pm = packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            val tempList = mutableListOf<InstalledApp>()

            for (packageInfo in packages) {

                if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {

                    val appName = pm.getApplicationLabel(packageInfo).toString()
                    val icon = pm.getApplicationIcon(packageInfo)

                    tempList.add(
                        InstalledApp(appName, packageInfo.packageName, icon)
                    )
                }
            }

            runOnUiThread {
                appList.clear()
                appList.addAll(tempList)
                binding.recyclerApps.adapter?.notifyDataSetChanged()
            }

        }.start()
    }

}
