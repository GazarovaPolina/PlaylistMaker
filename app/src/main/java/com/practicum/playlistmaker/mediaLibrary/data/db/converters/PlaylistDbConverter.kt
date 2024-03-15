package com.practicum.playlistmaker.mediaLibrary.data.db.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImageUrl = playlist.imageUrl,
            playlistTracksIds = playlist.tracksIds,
            countPlaylistTracks = playlist.countTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imageUrl = playlist.playlistImageUrl,
            tracksIds = playlist.playlistTracksIds,
            countTracks = playlist.countPlaylistTracks
        )
    }

    fun map(track: Track): TrackInPlaylistEntity {
        val currentTimestamp: Long = java.util.Date().time
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.artworkUrl100,
            timestamp = currentTimestamp
        )
    }
}