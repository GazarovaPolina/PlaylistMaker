package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        repository.searchTracks(expression)
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is SearchResult.Success -> {
                    Pair(result.result, null)
                }

                is SearchResult.Failure -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}