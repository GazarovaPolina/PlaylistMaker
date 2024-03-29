package com.practicum.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.main.MainViewEvents
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val state = MainViewEvents<ScreenState>()
    val screenState: LiveData<ScreenState> = state

    fun onSearchBtnClicked() {
        state.value = ScreenState.Search
    }

    fun onMediaLibBtnClicked() {
        state.value = ScreenState.MediaLib
    }

    fun onSettingsBtnClicked() {
        state.value = ScreenState.Settings
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel()
            }
        }
    }
}
