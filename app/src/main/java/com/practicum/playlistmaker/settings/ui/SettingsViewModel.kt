package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.AppThemeSwitcher
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.settings.domain.AppTheme
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.ExternalActions

class SettingsViewModel(
    private val externalActions: ExternalActions,
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

    fun onShareAppLinkClicked() {
        externalActions.shareAppLink()
    }

    fun onAgreementClicked() {
        externalActions.openAgreement()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    externalActions = Creator.getExternalActions(),
                    settingsInteractor = Creator.provideSettingsInteractor()
                )
            }
        }
    }
}