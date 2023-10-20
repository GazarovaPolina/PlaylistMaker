package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchState {

    class  LoadState: SearchState

    class ContentState(
        val tracks: List<Track>
    ) : SearchState

    class HistoryState(
        val tracks: Array<Track>?
    ): SearchState

    class EmptyState(
        val errorMsg: String
    ) : SearchState

    class ErrorState(
        val errorMsg: String
    ): SearchState

    class UpdateState(
        val tracks: Array<Track>?
    ): SearchState
}