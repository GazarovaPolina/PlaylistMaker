package com.practicum.playlistmaker.player.domain

sealed class TrackInPlaylistState(val message: String, val isTrackInPlaylist: Boolean) {
    class TrackInPlaylist(message: String): TrackInPlaylistState(message, true)
    class TrackNotInPlaylist(message: String): TrackInPlaylistState(message, false)
}