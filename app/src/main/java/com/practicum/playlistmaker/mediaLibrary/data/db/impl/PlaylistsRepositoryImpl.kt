package com.practicum.playlistmaker.mediaLibrary.data.db.impl

import com.google.gson.Gson
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun getPlaylistDetailsById(id: Long): Playlist {
        val playlistEntity = appDatabase.playlistDao().getPlaylistDetailsById(id)
        return playlistDbConverter.map(playlistEntity)
    }

    override fun getPlaylistTracksList(tracksIdsList: List<Int>): Flow<List<Track>> = flow {
        val tracksInPlaylistEntities = appDatabase.trackInPlaylistDao().getPlaylistTracksList(tracksIdsList)
        val sortedTracksInPlaylistEntities = tracksInPlaylistEntities.sortedByDescending { it.timestamp }
        val tracksInPlaylist = sortedTracksInPlaylistEntities.map { track -> playlistDbConverter.map(track) }
        emit(tracksInPlaylist)
    }

    override suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean {
        val allTracks = getListOfPlaylists()
        var isTrackInPlaylist = false

//        listOf(1, 2, 0, 3).any { it == 0 }

        allTracks.collect { playlistList ->
            for (playlist in playlistList) {
                val tracksInPlaylist = Gson().fromJson(playlist.tracksIds, Array<Long>::class.java)?: emptyArray()
                if (trackId in tracksInPlaylist) {
                    isTrackInPlaylist = true
                    break
                }
            }
        }
        return isTrackInPlaylist
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = playlistDbConverter.map(track)
        appDatabase.trackInPlaylistDao().deleteTrack(trackEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}