package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is SearchResult.Success -> {
                    consumer.consume(resource.result, null)
                }

                is SearchResult.Failure -> {
                    consumer.consume(null, resource.errorMsg)
                }
            }
        }
    }
}
