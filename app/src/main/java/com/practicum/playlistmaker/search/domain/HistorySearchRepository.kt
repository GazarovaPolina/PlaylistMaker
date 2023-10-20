package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface HistorySearchRepository {

    fun addTrackToHistory(track: Track)

    fun getTracksFromHistory(): Array<Track>

    fun clearSearchHistory()
}