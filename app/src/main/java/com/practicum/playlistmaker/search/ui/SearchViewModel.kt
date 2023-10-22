package com.practicum.playlistmaker.search.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchResult
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

    init {
        makeState(SearchState.HistoryState(historySearchInteractor.getTracksFromHistory()))
    }

    private fun makeState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun getTracksFromHistory() {
        makeState(SearchState.HistoryState(historySearchInteractor.getTracksFromHistory()))
    }

    fun addTrackToHistory(track: Track) {
        historySearchInteractor.addTrackToHistory(track)
    }

    fun clear() {
        historySearchInteractor.clearSearchHistory()
    }

    fun searchDebounce(changedText: String) {
        this.lastSearchText = changedText
        searchDebounce.searchDebounce { searchRequest(changedText) }
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
                        when (foundTracks) {
                            is SearchResult.Failure -> makeState(SearchState.ErrorState(R.string.bad_connection))
                            is SearchResult.Success -> makeState(SearchState.ContentState(foundTracks.result))
                        }
                    }
                })
        }

    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    historySearchInteractor = Creator.provideSearchHistory(),
                    searchDebounce = Creator.getSearchDebounce(),
                    tracksInteractor = Creator.provideTracksInteractor(),
                )
            }
        }
    }
}