package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeSettings(): AppTheme
    fun setThemeSettings(appTheme: AppTheme)
}