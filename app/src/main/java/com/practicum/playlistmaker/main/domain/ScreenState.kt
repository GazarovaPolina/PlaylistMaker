package com.practicum.playlistmaker.main.domain

sealed interface ScreenState{

    data object Search : ScreenState
    data object MediaLib : ScreenState
    data object Settings : ScreenState
}
