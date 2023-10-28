package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track

class MediaPlayerViewModel(
    private val mediaPlayer: MediaPlayerInteractor,
    track: Track
) : ViewModel() {

    private val mediaPlayerState = MutableLiveData<MediaPlayerActivityState>()
    val playerState: LiveData<MediaPlayerActivityState> = mediaPlayerState
    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())

    init {
        mediaPlayerState.postValue(MediaPlayerActivityState.playerPreparedState(track))
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState.postValue(MediaPlayerActivityState.playerPreparedState(track))
            mainThreadHandler?.removeCallbacksAndMessages(null)
        }
    }

    fun preparePlayer() {
        mediaPlayer.prepareMediaPlayer()
    }

    private fun startPlayer() {
        mediaPlayer.startMediaPlayer()
        mediaPlayerState.postValue(mediaPlayer.let {
            MediaPlayerActivityState.playerPlayState(it.currentPosition())
        })
    }

    fun pausePlayer() {
        mediaPlayer.pauseMediaPlayer()
        mediaPlayerState.postValue(MediaPlayerActivityState.playerPauseState)
    }

    fun playerStop() {
        mediaPlayer.stopMediaPlayer()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    fun playPauseControl() {
        when (mediaPlayer.getPlayerState()) {
            MediaPlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {}
        }

        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayer.getPlayerState()) {
                    MediaPlayerState.STATE_PLAYING -> {
                        val time = mediaPlayer.currentPosition()
                        mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
                        mediaPlayerState.value =
                            MediaPlayerActivityState.playerPlayState(time)
                    }

                    MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    else -> {}
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 10L

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return track.previewUrl?.let { Creator.provideMediaPlayerInteractor(trackPreviewUrl = it) }
                        ?.let {
                            MediaPlayerViewModel(
                                track = track,
                                mediaPlayer = it
                            )
                        } as T
                }
            }

    }
}