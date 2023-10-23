package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: SearchResult<List<Track>>)
    }
}