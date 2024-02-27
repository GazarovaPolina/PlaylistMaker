package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track

class MediaPlayerViewModel(
    private val mediaPlayer: MediaPlayerInteractor,
    private val track: Track
) : ViewModel() {

    private val mediaPlayerState = MutableLiveData<MediaPlayerActivityState>()
    val playerState: LiveData<MediaPlayerActivityState> = mediaPlayerState
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    init {
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPreparedState(track))
        }
    }

    fun preparePlayer() {
        mediaPlayerState.value = MediaPlayerActivityState.PlayerPreparedState(track)
        track.previewUrl?.let { mediaPlayer.prepareMediaPlayer(it) }
    }

    private fun startPlayer() {
        mediaPlayer.startMediaPlayer()
        mediaPlayerState.postValue(mediaPlayer.let {
            MediaPlayerActivityState.PlayerPlayState(it.currentPosition())
        })
    }

    fun pausePlayer() {
        mediaPlayer.pauseMediaPlayer()
        mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPauseState)
    }

    fun playerStop() {
        mediaPlayer.stopMediaPlayer()
        mainThreadHandler.removeCallbacks(createUpdateTimerTask())
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

        mainThreadHandler.post(
            createUpdateTimerTask()
        )
    }

    fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayer.getPlayerState()) {
                    MediaPlayerState.STATE_PLAYING -> {
                        val time = mediaPlayer.currentPosition()
                        mainThreadHandler.postDelayed(this, DELAY_MILLIS)
                        mediaPlayerState.value =
                            MediaPlayerActivityState.PlayerPlayState(time)
                    }

                    MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                        mainThreadHandler.removeCallbacks(this)
                    }

                    else -> {}
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 10L
    }
}