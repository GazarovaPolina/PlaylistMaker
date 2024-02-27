package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.search.domain.models.Track

sealed class MediaPlayerActivityState {
    data class PlayerPreparedState(val track: Track) : MediaPlayerActivityState()
    data class PlayerPlayState(val playTime: String) : MediaPlayerActivityState()
    data object PlayerPauseState: MediaPlayerActivityState()
}
