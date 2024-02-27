package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerRepositoryImpl : MediaPlayerRepository {
    private lateinit var mediaPlayer: MediaPlayer

    override var playerState = MediaPlayerState.STATE_DEFAULT

    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun prepareMediaPlayer(trackPreviewUrl: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = MediaPlayerState.STATE_PREPARED
            onPreparedListener?.invoke()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = MediaPlayerState.STATE_PREPARED
            onCompletionListener?.invoke()
        }
    }

    override fun startMediaPlayer() {
        mediaPlayer.start()
        playerState = MediaPlayerState.STATE_PLAYING
    }

    override fun pauseMediaPlayer() {
        mediaPlayer.pause()
        playerState = MediaPlayerState.STATE_PAUSED
    }

    override fun stopMediaPlayer() {
        mediaPlayer.stop()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
    }

    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        onCompletionListener = listener
    }
}