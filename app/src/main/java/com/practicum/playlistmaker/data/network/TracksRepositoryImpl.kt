package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkCliet: NetworkClient) : TracksRepository {

    companion object {
        private const val TIME_FORMAT = "mm:ss"
        private const val IMAGE_QUALITY = "512x512bb.jpg"
    }

    override fun searchTracks(expression: String): List<Track> {

        val response = networkCliet.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    /*SimpleDateFormat(
                        TIME_FORMAT,
                        Locale.getDefault()
                    ).format(*/it.trackTime/*)*/,
                    it.artworkUrl100?.replaceAfterLast('/', IMAGE_QUALITY).toString(),
                    it.collectionName,
                    it.releaseDate?.subSequence(0, 4) as String?,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }

        } else {
            return emptyList()
        }
    }
}