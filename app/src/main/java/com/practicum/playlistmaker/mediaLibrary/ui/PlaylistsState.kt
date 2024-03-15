package com.practicum.playlistmaker.mediaLibrary.ui

import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist

sealed class PlaylistsState {
    data object Empty : PlaylistsState()
    class Content(val playlists: List<Playlist>) : PlaylistsState()
}