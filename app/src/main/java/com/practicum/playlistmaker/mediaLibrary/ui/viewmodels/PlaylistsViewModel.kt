package com.practicum.playlistmaker.mediaLibrary.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val _playlistsState = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = _playlistsState

    init {
        getPlaylists()
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getListOfPlaylists().collect() { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        _playlistsState.postValue(state)
    }
}