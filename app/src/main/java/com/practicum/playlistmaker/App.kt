package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.ui.switchTheme

const val APP_PREFERENCES = "app_preferences"
const val THEME_KEY = "theme_key"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val darkTheme = if (sharedPrefs.contains(THEME_KEY)) {
            sharedPrefs.getBoolean(THEME_KEY, false)
        } else {
            null
        }
        darkTheme?.let {
            switchTheme(darkTheme)
        }
    }
}