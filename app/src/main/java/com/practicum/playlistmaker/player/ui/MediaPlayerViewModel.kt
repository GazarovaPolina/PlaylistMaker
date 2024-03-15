package com.practicum.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.favorites.FavoritesInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _mediaPlayerState = MutableLiveData<MediaPlayerActivityState>()
    val mediaPlayerState: LiveData<MediaPlayerActivityState> = _mediaPlayerState

    private val _favoritesState = MutableLiveData<Boolean>()
    val favoritesState: LiveData<Boolean> = _favoritesState

    private val updateTimerDelayTimeMillis = 300L

    private var timerJob: Job? = null


    init {
        viewModelScope.launch {
            val ids = favoritesInteractor.getTrackIds()
            _favoritesState.value = track.trackId in ids
            Log.d("timestamp1", ids.joinToString { " " })
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            _mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPreparedState(track))
        }
    }

    fun preparePlayer() {
        _mediaPlayerState.value = MediaPlayerActivityState.PlayerPreparedState(track)
        track.previewUrl?.let { mediaPlayer.prepareMediaPlayer(it) }
    }

    private fun startPlayer() {
        mediaPlayer.startMediaPlayer()
        _mediaPlayerState.postValue(mediaPlayer.let {
            MediaPlayerActivityState.PlayerPlayState(it.currentPosition())
        })
    }

    fun pausePlayer() {
        mediaPlayer.pauseMediaPlayer()
        timerJob?.cancel()
        _mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPauseState(mediaPlayer.currentPosition()))
    }

    fun playerStop() {
        mediaPlayer.stopMediaPlayer()
    }

    fun playPauseControl() {
        when (mediaPlayer.getPlayerState()) {
            MediaPlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                startPlayer()
                createUpdateTimer()
            }

            else -> {}
        }
    }

    fun onDestroy() {
        mediaPlayer.release()
    }

    private fun createUpdateTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.getPlayerState() == MediaPlayerState.STATE_PLAYING) {
                val time = mediaPlayer.currentPosition()
                _mediaPlayerState.value = MediaPlayerActivityState.PlayerPlayState(time)
                delay(updateTimerDelayTimeMillis)
            }
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (_favoritesState.value == true) {
                favoritesInteractor.deleteTrackFromFavorites(track)
            } else {
                favoritesInteractor.addTrackToFavorites(track)
            }
            _favoritesState.value = _favoritesState.value != true
        }
    }
}