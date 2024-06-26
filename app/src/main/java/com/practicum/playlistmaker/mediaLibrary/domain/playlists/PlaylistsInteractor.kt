package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun insertNewPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track)
    suspend fun getTracksIds(id: Long): String
    fun getListOfPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistDetailsById(id: Long): Playlist
    fun getPlaylistTracksList(tracksIdsList: List<Int>): Flow<List<Track>>
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean
    suspend fun deleteTrack(track: Track)
    suspend fun deletePlaylist(playlist: Playlist)
}