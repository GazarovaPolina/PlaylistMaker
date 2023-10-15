package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.MediaPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerRepositoryImpl(private val trackPreviewUrl: String) : MediaPlayerRepository {

    private var mediaPlayer = MediaPlayer()

    override var playerState = MediaPlayerState.STATE_DEFAULT

    override fun prepareMediaPlayer() {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        setOnPreparedListener {
            playerState = MediaPlayerState.STATE_PREPARED
        }
        setOnCompletionListener {
            playerState = MediaPlayerState.STATE_PREPARED
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
        mediaPlayer.setOnPreparedListener{ listener?.invoke()}
    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnCompletionListener { listener?.invoke() }
    }
}