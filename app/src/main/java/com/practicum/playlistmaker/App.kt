package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.initApp(this)
        val settingsInteractor = Creator.provideSettingsInteractor()
        darkTheme = settingsInteractor.getThemeSettings().darkTheme

        AppThemeSwitcher.switchTheme(darkTheme)
    }
}