package com.practicum.playlistmaker.mediaLibrary.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class NewPlaylistCreationModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    private val _newPlaylistState = MutableLiveData(false)

    fun observeState(): LiveData<Boolean> = _newPlaylistState

    fun createNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertNewPlaylist(playlist)
        }
    }
}