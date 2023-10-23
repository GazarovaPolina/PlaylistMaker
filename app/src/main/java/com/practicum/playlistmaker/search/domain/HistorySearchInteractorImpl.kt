package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.api.HistorySearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class HistorySearchInteractorImpl(private val historySearchRepository: HistorySearchRepository): HistorySearchInteractor {

    override fun addTrackToHistory(track: Track) {
        historySearchRepository.addTrackToHistory(track)
    }

    override fun getTracksFromHistory(): List<Track> {
        return historySearchRepository.getTracksFromHistory()
    }

    override fun clearSearchHistory() {
        historySearchRepository.clearSearchHistory()
    }
}