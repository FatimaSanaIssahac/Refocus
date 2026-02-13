package com.example.myapplication

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.TextView

class OverlayManager(private val context: Context) {

    private var windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var overlayView: View? = null

    fun showOverlay(appName: String) {

        if (overlayView != null) return

        if (!Settings.canDrawOverlays(context)) return

        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        overlayView = inflater.inflate(R.layout.overlay_blocking, null)

        val blockText = overlayView!!.findViewById<TextView>(R.id.blockText)
        val closeButton = overlayView!!.findViewById<Button>(R.id.btnClose)

        blockText.text = "$appName limit reached.\nGo build your future."

        closeButton.setOnClickListener {
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

    fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    fun isOverlayVisible(): Boolean {
        return overlayView != null
    }
}
