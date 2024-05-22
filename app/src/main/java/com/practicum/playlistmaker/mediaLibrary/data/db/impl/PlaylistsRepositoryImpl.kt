package com.practicum.playlistmaker.mediaLibrary.data.db.impl

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistsRepository {

    override suspend fun insertNewPlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
        }
    }

    override suspend fun updateListOfPlaylists(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
        }
    }

    override suspend fun addTrackToPlaylist(track: Track) {
        appDatabase.trackInPlaylistDao().insertTrackToPlaylist(playlistDbConverter.map(track))
    }

    override suspend fun getTracksIds(id: Long): String =
        withContext(Dispatchers.IO) {
            appDatabase.playlistDao().getTracksIds(id)
        }


    override fun getListOfPlaylists(): Flow<List<Playlist>> = appDatabase
        .playlistDao()
        .getListOfPlaylists()
        .map { convertFromPlaylistEntity(it) }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}