package com.practicum.playlistmaker.player.domain
import com.practicum.playlistmaker.search.domain.models.Track

sealed class MediaPlayerActivityState(val playTime: String?) {
    class PlayerPreparedState(val track: Track) : MediaPlayerActivityState(null)
    class PlayerPlayState(playTime: String) : MediaPlayerActivityState(playTime)
    class PlayerPauseState(playTime: String) : MediaPlayerActivityState(playTime)
}
