package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.SearchResult
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

    //private val handler: Handler = Handler(Looper.getMainLooper())

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
        if (this.lastSearchText == changedText) {
            return
        }
        if (changedText.isEmpty()) {
            updateStateWithTracksFromHistory()
        } else {
            this.lastSearchText = changedText
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(changedText)
            }
            //searchDebounce { searchRequest(changedText) }
        }
    }


//    override fun onCleared() {
//        super.onCleared()
//        handler.removeCallbacksAndMessages(SEARCH_REQUEST)
//    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            makeState(SearchState.LoadState)
            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: SearchResult<List<Track>>) {

//                        handler.post {
//                            when (foundTracks) {
//                                is SearchResult.Failure -> makeState(SearchState.ErrorState(R.string.bad_connection))
//                                is SearchResult.Success -> makeState(SearchState.ContentState(foundTracks.result))
//                            }
//                        }

                        searchJob = viewModelScope.launch {
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

//    fun searchDebounce(request: () -> Unit) {
//        val searchRunnable = Runnable { request() }
//        handler.removeCallbacksAndMessages(SEARCH_REQUEST)
//        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
//        handler.postAtTime(
//            searchRunnable,
//            SEARCH_REQUEST,
//            postTime,
//        )
//    }


    companion object {
        private val SEARCH_REQUEST = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}