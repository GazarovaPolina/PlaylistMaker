package com.practicum.playlistmaker.mediaLibrary.domain.favorites

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getTrackIds(): List<Long>
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
}