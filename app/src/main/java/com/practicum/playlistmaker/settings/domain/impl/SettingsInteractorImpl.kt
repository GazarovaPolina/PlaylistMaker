package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.AppTheme
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository): SettingsInteractor {

    override fun getThemeSettings(): AppTheme = settingsRepository.getThemeSettings()

    override fun setThemeSettings(appTheme: AppTheme) {
        settingsRepository.setThemeSettings(appTheme)
    }
}