package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String) : Flow<SearchResult<List<Track>>>
}