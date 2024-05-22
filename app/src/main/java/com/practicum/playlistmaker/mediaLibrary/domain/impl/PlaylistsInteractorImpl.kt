package com.practicum.playlistmaker.mediaLibrary.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {

    override suspend fun insertNewPlaylist(playlist: Playlist) {
        return playlistsRepository.insertNewPlaylist(playlist)
    }

    override suspend fun updateListOfPlaylists(playlist: Playlist) {
        return playlistsRepository.updateListOfPlaylists(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track) {
        return playlistsRepository.addTrackToPlaylist(track)
    }

    override suspend fun getTracksIds(id: Long): String {
        return playlistsRepository.getTracksIds(id)
    }

    override fun getListOfPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getListOfPlaylists()
    }
}