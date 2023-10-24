package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private var query: String? = null
    private var searchResultAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private var isSearchResultClickEnable = true

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        setToolbarIconOnClickListener()

        setBtnClearHistoryOnClickListener()

        setIconClearOnClickListener()

        binding.btnUpdate.setOnClickListener {
            viewModel.searchDebounce(query ?: "")
        }

        setOnSearchResultItemClick()

        setOnHistoryTrackClick()

        setSearchQueryChangedListener()

        viewModel.observeState().observe(this) {
            executeAction(it)
        }
    }

    private fun setToolbarIconOnClickListener() {
        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setIconClearOnClickListener() {
        binding.iconClear.setOnClickListener {
            viewModel.onItemClearSearchQueryClick()
        }
    }

    private fun updateTracksList(newTrackList: List<Track>) {
        searchResultAdapter.tracks.clear()
        searchResultAdapter.tracks.addAll(newTrackList)
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun setBtnClearHistoryOnClickListener() {
        binding.btnClearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun setOnSearchResultItemClick() {
        searchResultAdapter.onItemClick = {
            if (searchResClickDebounce()) {
                viewModel.onSearchResultItemClick(it)
                displayAudioPlayer(it)
            }
        }
    }

    private fun setOnHistoryTrackClick() {
        historyAdapter.onItemClick = {
            if (searchResClickDebounce()) {
                viewModel.onHistoryTrackClick(it)
                displayAudioPlayer(it)
            }
        }
    }

    private fun searchResClickDebounce(): Boolean {
        val current = isSearchResultClickEnable
        if (isSearchResultClickEnable) {
            isSearchResultClickEnable = false
            handler.postDelayed({ isSearchResultClickEnable = true }, SEARCH_RES_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun displayAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun setSearchQueryChangedListener() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(editable: Editable?) {
                val queryString = editable?.toString()
                if (query != queryString) {
                    query = queryString
                    viewModel.searchDebounce(queryString ?: "")
                }
            }
        })
    }


    private fun executeAction(state: SearchState) {
        binding.iconClear.isVisible = state is SearchState.ContentState || state is SearchState.LoadState || state is SearchState.ErrorState
        binding.btnUpdate.isVisible = state is SearchState.ErrorState
        binding.recyclerViewTracks.isVisible =
            state is SearchState.HistoryState && state.tracks.isNotEmpty() || state is SearchState.ContentState && state.tracks.isNotEmpty()
        binding.btnClearHistory.isVisible = state is SearchState.HistoryState
        binding.searchHistoryMessage.isVisible = state is SearchState.HistoryState && state.tracks.isNotEmpty()
        binding.searchProgressBar.isVisible = state is SearchState.LoadState
        binding.placeholderImage.isVisible = state is SearchState.ErrorState || state is SearchState.ContentState && state.tracks.isEmpty()
        binding.placeholderMessage.isVisible = state is SearchState.ErrorState || state is SearchState.ContentState && state.tracks.isEmpty()
        binding.btnClearHistory.isVisible = state is SearchState.HistoryState && !state.tracks.isNullOrEmpty()

        when (state) {
            is SearchState.ContentState -> displayTracksList(state.tracks)
            is SearchState.ErrorState -> displayBadConnectionError(state.errorMsgResId)
            is SearchState.HistoryState -> loadHistory(state.tracks)
            else -> {}
        }
    }

    private fun displayTracksList(foundTracks: List<Track>) {
        if (foundTracks.isEmpty()) {
            displayNothingFoundMessage()
            return
        }
        binding.recyclerViewTracks.adapter = searchResultAdapter
        updateTracksList(foundTracks)
    }


    private fun displayBadConnectionError(@StringRes errorMsgResId: Int) {
        binding.placeholderImage.setImageResource(R.drawable.ic_bad_connection)
        binding.placeholderMessage.text = getString(errorMsgResId)
    }

    private fun displayNothingFoundMessage() {
        binding.placeholderImage.setImageResource(R.drawable.ic_placeholder_nothing_found)
        binding.placeholderMessage.text = getString(R.string.nothing_found)
    }

    private fun loadHistory(historyTracks: List<Track>?) {
        binding.editTextSearch.setText("")
        if (historyTracks.isNullOrEmpty()) {
        } else {
            historyAdapter.tracks = historyTracks.toCollection(ArrayList())
            binding.recyclerViewTracks.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val query = savedInstanceState.getString(SEARCH_QUERY)
        binding.editTextSearch.setText(query)
        this.query = query
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, query)
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_RES_CLICK_DEBOUNCE_DELAY = 1000L
    }
}
