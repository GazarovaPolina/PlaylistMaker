package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.domain.models.Track

sealed class MediaPlayerActivityState {

    data class playerPreparedState(val track: Track) : MediaPlayerActivityState()

    data class playerPlayState(val playTime: String) : MediaPlayerActivityState()
    data object playerPauseState: MediaPlayerActivityState()
}
