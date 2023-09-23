package com.practicum.playlistmaker.domain.api

class MediaPlayerInteractor(private val mediaPlayer: MediaPlayerRepository) {

    fun prepareMediaPlayer(url: String) {
        mediaPlayer.prepareMediaPlayer(trackUrl = url)
    }

    fun startMediaPlayer() {
        mediaPlayer.startMediaPlayer()
    }

    fun pauseMediaPlayer() {
        mediaPlayer.pauseMediaPlayer()
    }


    fun release() {
        mediaPlayer.release()
    }

    fun currentPosition(): Int {
        return mediaPlayer.currentPosition()
    }

    fun setOnPreparedListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnPreparedListener(listener)
    }

    fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnCompletionListener(listener)
    }
}