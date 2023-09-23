package com.practicum.playlistmaker.domain.api

interface MediaPlayerRepository {

    fun prepareMediaPlayer(trackUrl: String)

    fun startMediaPlayer()

    fun pauseMediaPlayer()

    fun release()

    fun currentPosition(): Int

    fun setOnPreparedListener(listener: (() -> Unit)?)

    fun setOnCompletionListener(listener: (() -> Unit)?)
}