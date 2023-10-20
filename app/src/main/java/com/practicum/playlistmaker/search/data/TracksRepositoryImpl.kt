package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.SearchResult
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): SearchResult<List<Track>> {

        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            200 -> {
                return SearchResult.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTime,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }
            -1 -> {
                return SearchResult.Failure("Проблемы со связью. Загрузка не удалась. Проверьте подключение к интернету")
            }
            else -> {
                return SearchResult.Failure("Что-то пошло не так")
            }
        }
    }
}