package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    private val mediaPlayer: MediaPlayerInteractor,
    private val track: Track
) : ViewModel() {

    private val mediaPlayerState = MutableLiveData<MediaPlayerActivityState>()
    val playerState: LiveData<MediaPlayerActivityState> = mediaPlayerState
    //private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    private var timerJob: Job? = null

    init {
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPreparedState(track))
            timerJob?.cancel()
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
        timerJob?.cancel()
        mediaPlayerState.postValue(MediaPlayerActivityState.PlayerPauseState)
    }

    fun playerStop() {
        mediaPlayer.stopMediaPlayer()
        //mainThreadHandler.removeCallbacks(createUpdateTimerTask())
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

//        mainThreadHandler.post(
//            createUpdateTimerTask()
//        )
//        timerJob = viewModelScope.launch {
//            createUpdateTimerTask()
//        }


    }

    fun onDestroy() {
        mediaPlayer.release()
        //mainThreadHandler.removeCallbacksAndMessages(null)
    }

//    private fun createUpdateTimerTask(): Runnable {
//        return object : Runnable {
//            override fun run() {
//                when (mediaPlayer.getPlayerState()) {
//                    MediaPlayerState.STATE_PLAYING -> {
//                        val time = mediaPlayer.currentPosition()
//                        mainThreadHandler.postDelayed(this, DELAY_MILLIS)
//                        mediaPlayerState.value =
//                            MediaPlayerActivityState.PlayerPlayState(time)
//                    }
//
//                    MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
//                        mainThreadHandler.removeCallbacks(this)
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }

    private fun createUpdateTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.getPlayerState() == MediaPlayerState.STATE_PLAYING) {
                val time = mediaPlayer.currentPosition()
                delay(300)
                mediaPlayerState.value = MediaPlayerActivityState.PlayerPlayState(time)
            }

        }

    }

//    companion object {
//        private const val DELAY_MILLIS = 10L
//    }
}