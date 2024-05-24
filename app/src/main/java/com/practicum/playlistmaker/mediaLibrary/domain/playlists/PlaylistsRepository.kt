package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun insertNewPlaylist(playlist: Playlist)
    suspend fun updateListOfPlaylists(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track)
    suspend fun getTracksIds(id: Long): String
    fun getListOfPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistDetailsById(id: Long): Playlist
    fun getPlaylistTracksList(tracksIdsList: List<Int>): Flow<List<Track>>
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean
    suspend fun deleteTrack(track : Track)
}