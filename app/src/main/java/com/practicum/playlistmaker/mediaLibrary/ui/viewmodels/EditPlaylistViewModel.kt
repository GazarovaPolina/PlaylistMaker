package com.practicum.playlistmaker.mediaLibrary.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistId: String, private val playlistsInteractor: PlaylistsInteractor) : NewPlaylistCreationModel(playlistsInteractor) {

    private val _playlistDetailsToEdit = MutableLiveData<Playlist>()
    val playlistDetailsToEdit: LiveData<Playlist> get() = _playlistDetailsToEdit


     fun getPlaylist() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistDetailsById(playlistId.toLong())
            _playlistDetailsToEdit.postValue(playlist)
        }
    }

    fun saveEditedPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist)
        }
    }
}