package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LimitsStorage {

    private const val PREFS_NAME = "limits_prefs"
    private const val LIMITS_KEY = "app_limits"
    private val gson = Gson()

    fun saveLimits(context: Context, limits: List<AppLimit>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(limits)
        prefs.edit().putString(LIMITS_KEY, json).apply()
    }

    fun getLimits(context: Context): List<AppLimit> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(LIMITS_KEY, "[]") ?: "[]"
        val type = object : TypeToken<List<AppLimit>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
