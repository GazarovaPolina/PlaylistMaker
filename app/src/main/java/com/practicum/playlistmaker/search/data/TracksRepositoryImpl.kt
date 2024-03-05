package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<SearchResult<List<Track>>> = flow {

        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            200 -> {
                with(response as TracksSearchResponse) {
                    val data = results.map {
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
                    }
                    emit(SearchResult.Success(data))
                }
            }

            else -> {
                emit(SearchResult.Failure(ServerErrorMessage.SERVER_ERROR.message))
            }
        }
    }
}

enum class ServerErrorMessage(val message: String) {
    SERVER_ERROR("Error message")
}