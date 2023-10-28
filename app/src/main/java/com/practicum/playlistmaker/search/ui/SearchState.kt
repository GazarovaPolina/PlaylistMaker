package com.practicum.playlistmaker.search.ui

import androidx.annotation.StringRes
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    data object LoadState : SearchState

    class ContentState(
        val tracks: List<Track>
    ) : SearchState

    class HistoryState(
        val tracks: List<Track>
    ): SearchState

    class ErrorState(
       @StringRes val errorMsgResId: Int
    ): SearchState
}