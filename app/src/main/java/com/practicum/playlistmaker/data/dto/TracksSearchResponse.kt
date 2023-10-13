package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

class TracksSearchResponse(val results: List<Track>) : Response() //TODO: Change Track to TrackDto after cleaning SearchActivity