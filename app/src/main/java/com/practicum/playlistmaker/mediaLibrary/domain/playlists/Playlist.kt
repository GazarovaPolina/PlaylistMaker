package com.practicum.playlistmaker.mediaLibrary.domain.playlists

data class Playlist(
    val id: Long?,
    val playlistName: String,
    val playlistDescription: String?,
    val imageUrl: String?,
    val tracksIds: String?,
    val countTracks: Int
)
