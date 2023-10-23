package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistorySearchInteractor {

    fun addTrackToHistory(track: Track)

    fun getTracksFromHistory(): List<Track>

    fun clearSearchHistory()
}