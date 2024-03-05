package com.practicum.playlistmaker.mediaLibrary.data.db

import com.practicum.playlistmaker.mediaLibrary.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> =  flow {
        val tracks = appDatabase.trackDao().getTracksInfo()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun getTrackIds(): List<Long> {
        return appDatabase.trackDao().getTracksIds()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}