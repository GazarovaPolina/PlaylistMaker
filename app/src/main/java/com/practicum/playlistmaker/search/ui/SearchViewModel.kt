package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.HistorySearchInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    val historySearchInteractor: HistorySearchInteractor,
) : ViewModel() {
    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var lastSearchText: String? = null

    private var searchJob: Job? = null

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

        if (changedText.isEmpty()) {
            updateStateWithTracksFromHistory()
        } else {
            this.lastSearchText = changedText
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(changedText)
            }
        }
    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            makeState(SearchState.LoadState)

            viewModelScope.launch {

                tracksInteractor.searchTracks(
                    newSearchText
                ).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    fun processResult(foundTracks: List<Track>?, errorMessage: String?) {

        if (foundTracks != null) {
            makeState(SearchState.ContentState(foundTracks))
        }
        if (errorMessage != null) {
            makeState(SearchState.ErrorState(R.string.bad_connection))
        }
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}