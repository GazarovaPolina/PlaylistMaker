package com.practicum.playlistmaker.mediaLibrary.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed class MediaLibFavoritesState {
    data object Empty : MediaLibFavoritesState()
    class Content(val tracks: List<Track>): MediaLibFavoritesState()
}