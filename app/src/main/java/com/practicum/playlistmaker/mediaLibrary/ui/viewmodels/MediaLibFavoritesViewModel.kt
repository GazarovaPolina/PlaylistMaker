package com.practicum.playlistmaker.mediaLibrary.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.FavoritesInteractor
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibFavoritesState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class MediaLibFavoritesViewModel(private val favoritesInteractor: FavoritesInteractor): ViewModel() {

    private val mediaLibFavoritesState = MutableLiveData<MediaLibFavoritesState>()
    fun observeState(): LiveData<MediaLibFavoritesState> = mediaLibFavoritesState

    init {
        getTracks()
    }

    private fun getTracks() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(MediaLibFavoritesState.Empty)
        } else {
            renderState(MediaLibFavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: MediaLibFavoritesState) {
        mediaLibFavoritesState.postValue(state)
    }

    fun refreshFavorites() {
        getTracks()
    }
}