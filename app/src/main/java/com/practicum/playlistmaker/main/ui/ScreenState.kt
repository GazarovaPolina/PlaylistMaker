package com.practicum.playlistmaker.main.ui

sealed interface ScreenState{

    data object Search : ScreenState
    data object MediaLib : ScreenState
    data object Settings : ScreenState
}
