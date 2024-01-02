package com.practicum.playlistmaker.player.domain

interface MediaPlayerInteractor {

    fun prepareMediaPlayer(trackPreviewUrl: String)

    fun startMediaPlayer()

    fun pauseMediaPlayer()

    fun stopMediaPlayer()

    fun release()

    fun currentPosition(): String

    fun setOnPreparedListener(listener: (() -> Unit)?)

    fun setOnCompletionListener(listener: (() -> Unit)?)

    fun getPlayerState() : MediaPlayerState
}