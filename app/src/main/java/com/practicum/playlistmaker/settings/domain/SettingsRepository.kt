package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeSettings(): AppTheme
    fun setThemeSettings(appTheme: AppTheme)
}