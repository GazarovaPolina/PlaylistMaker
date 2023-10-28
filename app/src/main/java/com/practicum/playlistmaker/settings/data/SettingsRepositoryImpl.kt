package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.AppTheme
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(app: Application) : SettingsRepository {

    private val sharedPrefs: SharedPreferences =
        app.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

    override fun getThemeSettings(): AppTheme = AppTheme(sharedPrefs.getBoolean(THEME_KEY, false))

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