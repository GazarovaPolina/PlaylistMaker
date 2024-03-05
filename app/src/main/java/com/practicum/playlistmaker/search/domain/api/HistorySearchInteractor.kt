package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistorySearchInteractor {

    suspend fun addTrackToHistory(track: Track)

    suspend fun getTracksFromHistory(): List<Track>

    fun clearSearchHistory()
}