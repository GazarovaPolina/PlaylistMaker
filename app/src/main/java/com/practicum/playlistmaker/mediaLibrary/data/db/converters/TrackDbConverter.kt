package com.practicum.playlistmaker.mediaLibrary.data.db.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConverter {
    fun map(track: Track): TrackEntity {
        val currentTimestamp = System.currentTimeMillis()
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            timestamp = currentTimestamp
        )
    }

    fun map(trackEntity: TrackEntity): Track = Track(
        trackId = trackEntity.trackId,
        trackName = trackEntity.trackName,
        artistName = trackEntity.artistName,
        trackTime = trackEntity.trackTime,
        artworkUrl100 = trackEntity.artworkUrl100,
        collectionName = trackEntity.collectionName,
        releaseDate = trackEntity.releaseDate,
        primaryGenreName = trackEntity.primaryGenreName,
        country = trackEntity.country,
        previewUrl = trackEntity.previewUrl
    )
}