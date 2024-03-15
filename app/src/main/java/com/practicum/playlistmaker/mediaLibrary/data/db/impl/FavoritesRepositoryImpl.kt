package com.practicum.playlistmaker.mediaLibrary.data.db.impl

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.favorites.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = appDatabase
        .trackDao()
        .getTracksInfo()
        .map {
            convertFromTrackEntity(it)
        }
        .flowOn(Dispatchers.IO)

    override suspend fun getTrackIds(): List<Long> {
        var trackIds: List<Long>
        withContext(Dispatchers.IO) {
            trackIds = appDatabase.trackDao().getTracksIds()
        }
        return trackIds

    }

    override suspend fun addTrackToFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
        }
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
        }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}