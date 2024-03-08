package com.practicum.playlistmaker.mediaLibrary.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.FavoritesInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository): FavoritesInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun getTrackIds(): List<Long> {
        return favoritesRepository.getTrackIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        return favoritesRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        return favoritesRepository.deleteTrackFromFavorites(track)
    }
}