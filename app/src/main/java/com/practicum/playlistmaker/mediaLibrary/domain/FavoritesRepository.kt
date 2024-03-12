package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getTrackIds(): List<Long>
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
}