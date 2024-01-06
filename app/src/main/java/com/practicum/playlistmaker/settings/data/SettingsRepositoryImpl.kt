package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.AppTheme
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val context: Context, private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun getThemeSettings(): AppTheme {
        return if (sharedPrefs.contains(THEME_KEY)) {
            AppTheme(sharedPrefs.getBoolean(THEME_KEY, false))
        } else {
            val darkModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isDarkModeOn = darkModeFlags == Configuration.UI_MODE_NIGHT_YES
            return AppTheme(isDarkModeOn).also { setThemeSettings(it) }
        }
    }

    override fun setThemeSettings(appTheme: AppTheme) {
        sharedPrefs.edit {
            putBoolean(THEME_KEY, appTheme.darkTheme)
        }
    }


    companion object {
        const val APP_PREFERENCES = "app_preferences"
        const val THEME_KEY = "theme_key"
    }
}