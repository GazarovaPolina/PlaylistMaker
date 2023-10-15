package com.practicum.playlistmaker.player.domain

interface MediaPlayerRepository {

    var playerState: MediaPlayerState

    fun prepareMediaPlayer()

    fun startMediaPlayer()

    fun pauseMediaPlayer()

    fun stopMediaPlayer()

    fun release()

    fun currentPosition(): String

    fun setOnPreparedListener(listener: (() -> Unit)?)

    fun setOnCompletionListener(listener: (() -> Unit)?)
}