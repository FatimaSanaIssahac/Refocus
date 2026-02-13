package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.util.*
import android.app.usage.UsageStatsManager

class AppMonitorService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, 2000) // check every 2 seconds
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        handler.post(checkRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val channelId = "monitor_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Refocus Running")
            .setContentText("Monitoring app usage")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)
    }

    private fun checkForegroundApp() {

        if (!Settings.canDrawOverlays(this)) return

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 * 60

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )

        if (stats.isNullOrEmpty()) return

        val recentApp = stats.maxByOrNull { it.lastTimeUsed } ?: return
        val packageName = recentApp.packageName

        // Ignore our own app (Refocus)
        if (packageName == this.packageName) return

        // 🔥 CHECK YOUR SAVED LIMITS HERE
        val limits = LimitsStorage.getLimits(this)

        for (limit in limits) {
            if (limit.packageName == packageName) {

                val totalUsage = getTodayUsage(packageName)
                if (totalUsage >= limit.limitMinutes * 60 * 1000) {
                    showOverlay(limit.appName)
                }
            }
        }
    }

    private fun getTodayUsage(packageName: String): Long {

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val appStats = stats.find { it.packageName == packageName }
        return appStats?.totalTimeInForeground ?: 0
    }

    private fun showOverlay(appName: String) {

        if (overlayView != null) return

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_blocking, null)

        val text = overlayView!!.findViewById<TextView>(R.id.blockText)
        val button = overlayView!!.findViewById<Button>(R.id.btnClose)

        text.text = "$appName limit reached.\nGo build your future."

        button.setOnClickListener {
            removeOverlay()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)
    }

    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkRunnable)
        removeOverlay()
    }
}
