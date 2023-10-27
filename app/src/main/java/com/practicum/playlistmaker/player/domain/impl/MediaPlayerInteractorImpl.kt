package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.MediaPlayerState

class MediaPlayerInteractorImpl(
    private val mediaPlayer: MediaPlayerRepository): MediaPlayerInteractor {

    override fun prepareMediaPlayer(trackPreviewUrl: String) {
        mediaPlayer.prepareMediaPlayer(trackPreviewUrl)
    }

    override fun startMediaPlayer() {
        mediaPlayer.startMediaPlayer()
    }

    override fun pauseMediaPlayer() {
        mediaPlayer.pauseMediaPlayer()
    }

    override fun stopMediaPlayer() {
        mediaPlayer.stopMediaPlayer()
    }


    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): String {
        return mediaPlayer.currentPosition()
    }

    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnCompletionListener(listener)
    }

    override fun getPlayerState(): MediaPlayerState {
        return mediaPlayer.playerState
    }
}