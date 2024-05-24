package com.practicum.playlistmaker.mediaLibrary.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.player.ui.CountMessageEndingChanger
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(playlistId: String, private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    private val _playlistDetails = MutableLiveData<Playlist>()
    val playlistDetails: LiveData<Playlist> get() = _playlistDetails

    private val _playlistTracksLiveData = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracksLiveData

    private val _allTracksDurationLiveData = MutableLiveData<Long>()
    val tracksDuration: LiveData<Long> = _allTracksDurationLiveData



    init {
        getPlaylistById(playlistId.toLong())
    }

    private fun getPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistDetailsById(playlistId)
            _playlistDetails.postValue(playlist)
            val tracksIds = Gson().fromJson(playlist.tracksIds, Array<Int>::class.java) ?: emptyArray()
            playlistsInteractor.getPlaylistTracksList(tracksIds.toMutableList()).collect {
                tracks -> calculateAllTracksDuration(tracks)
            }
        }
    }

    private fun calculateAllTracksDuration(tracks: List<Track>) {
        val durationSum = tracks.sumOf { it.trackTime }
        val totalDuration = millisecondsToMinutes(durationSum)
        _allTracksDurationLiveData.postValue(totalDuration)
    }

    private fun millisecondsToMinutes(milliseconds: Long): Long {
        return milliseconds / 60000
    }




}