package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.SearchResult
import com.practicum.playlistmaker.search.domain.api.HistorySearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchDebounce
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchDebounce: SearchDebounce,
    val historySearchInteractor: HistorySearchInteractor,
) : ViewModel() {
    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var historyTracks = ArrayList<Track>()
    private val handler: Handler = Handler(Looper.getMainLooper())

    init {
        makeState(SearchState.HistoryState(historySearchInteractor.getTracksFromHistory()))
    }

    fun onItemClearSearchQueryClick() {
        updateStateWithTracksFromHistory()
    }

    fun onSearchResultItemClick(track: Track) {
        addTrackToHistory(track)
    }

    fun onHistoryTrackClick(track: Track) {
        addTrackToHistory(track)
        updateStateWithTracksFromHistory()
    }

    private fun makeState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private fun updateStateWithTracksFromHistory() {
        makeState(SearchState.HistoryState(historySearchInteractor.getTracksFromHistory()))
    }

    private fun addTrackToHistory(track: Track) {
        historySearchInteractor.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        historySearchInteractor.clearSearchHistory()
        updateStateWithTracksFromHistory()
    }

    fun searchDebounce(changedText: String) {
        this.lastSearchText = changedText
        if (changedText.isEmpty()) {
            updateStateWithTracksFromHistory()
        } else {
            searchDebounce.searchDebounce { searchRequest(changedText) }
        }
    }

    private var lastSearchText: String? = null

    override fun onCleared() {
        super.onCleared()
        searchDebounce.clear()
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            makeState(SearchState.LoadState)
            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: SearchResult<List<Track>>) {
                        handler.post {
                            when (foundTracks) {
                                is SearchResult.Failure -> makeState(SearchState.ErrorState(R.string.bad_connection))
                                is SearchResult.Success -> makeState(SearchState.ContentState(foundTracks.result))
                            }
                        }
                    }
                }
            )
        }
    }
}