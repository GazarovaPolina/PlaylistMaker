package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.Creator
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
            makeState(SearchState.LoadState())
            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val trackList = mutableListOf<Track>()
                        if (foundTracks != null) {
                            //trackList.clear()
                            trackList.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                val message =
                                    "Проблемы со связью \n\n Загрузка не удалась. Проверьте подключение к интернету"
                                makeState(SearchState.ErrorState(message))
                            }

                            trackList.isEmpty() -> {
                                val message = "Ничего не нашлось"
                                makeState(SearchState.EmptyState(message))
                            }

                            else -> {
                                makeState(SearchState.ContentState(trackList))
                            }
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