package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.AppThemeSwitcher
import com.practicum.playlistmaker.settings.domain.AppTheme
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val externalActions: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val themeSwitcherCondition = MutableLiveData<Boolean>()
    val themeSwitcherState: LiveData<Boolean> = themeSwitcherCondition
    private var darkTheme = false

    init {
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        themeSwitcherCondition.value = darkTheme
    }

    fun onThemeSwitcherClicked(isChecked: Boolean) {
        themeSwitcherCondition.value = isChecked
        settingsInteractor.setThemeSettings(AppTheme(darkTheme = isChecked))
        AppThemeSwitcher.switchTheme(isChecked)
    }

    fun onWriteToSupportClicked() {
        externalActions.writeToSupport()
    }

    fun onShareAppLinkClicked(text: String) {
        externalActions.share(text)
    }

    fun onAgreementClicked() {
        externalActions.openAgreement()
    }
}