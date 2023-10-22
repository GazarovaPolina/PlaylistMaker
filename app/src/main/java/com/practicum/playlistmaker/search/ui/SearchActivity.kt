package com.practicum.playlistmaker.search.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
    private var tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()
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

            tracks.clear()
            updateTracksList(tracks)
            viewModel.getTracksFromHistory()
            searchResultAdapter.notifyDataSetChanged()

            binding.editTextSearch.setText("")
            binding.placeholderMessage.isVisible = false
            binding.placeholderImage.isVisible = false
            binding.btnUpdate.isVisible = false
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun updateTracksList(newTrackList: List<Track>) {
        searchResultAdapter.tracks.clear()
        searchResultAdapter.tracks.addAll(newTrackList)
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun setBtnClearHistoryOnClickListener() {
        binding.btnClearHistory.setOnClickListener {
            viewModel.clear()
            hideSearchHistory()
        }
    }

    private fun setOnSearchResultItemClick() {
        searchResultAdapter.onItemClick = {
            if (searchResClickDebounce()) {
                viewModel.addTrackToHistory(it)
                displayAudioPlayer(it)
            }
        }
    }

    private fun setOnHistoryTrackClick() {
        historyAdapter.onItemClick = {
            if (searchResClickDebounce()) {
                viewModel.addTrackToHistory(it)
                viewModel.getTracksFromHistory()
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

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                binding.iconClear.visibility = clearButtonVisibility(s)

                query = s?.toString()
                viewModel.searchDebounce(query ?: "")

                if (binding.editTextSearch.hasFocus() && s?.isEmpty() == true) {
                    showSearchHistory()
                } else {
                    historyTracks.clear()
                    historyAdapter.notifyDataSetChanged()
                    hideSearchHistory()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    private fun showSearchHistory() {
        binding.searchHistoryMessage.isVisible = true
        binding.recyclerViewTracks.isVisible = true
        binding.btnClearHistory.isVisible = true
    }

    private fun hideSearchHistory() {
        binding.searchHistoryMessage.isVisible = false
        binding.recyclerViewTracks.isVisible = false
        binding.btnClearHistory.isVisible = false
    }

    private fun executeAction(state: SearchState) {
        when (state) {
            is SearchState.LoadState -> displaySearchProgressBar()
            is SearchState.ContentState -> displayTracksList(state.tracks)
            is SearchState.ErrorState -> displayBadConnectionError(state.errorMsgResId)
            is SearchState.HistoryState -> loadHistory(state.tracks?.toCollection(ArrayList()))
            is SearchState.UpdateState -> updateHistory(state.tracks?.toCollection(ArrayList()))
        }
    }

    private fun displaySearchProgressBar() {
        binding.searchProgressBar.isVisible = true
        binding.recyclerViewTracks.isVisible = false
        binding.placeholderImage.isVisible = false
        binding.placeholderMessage.isVisible = false
        binding.btnUpdate.isVisible = false
    }

    private fun displayTracksList(foundTracks: List<Track>) {
        if (foundTracks.isEmpty()) {
            displayNothingFoundMessage()
            return
        }
        binding.searchProgressBar.isVisible = false
        tracks.clear()
        tracks.addAll(foundTracks)
        binding.recyclerViewTracks.adapter = searchResultAdapter
        updateTracksList(tracks)
        binding.recyclerViewTracks.isVisible = true
    }


    private fun displayBadConnectionError(@StringRes  errorMsgResId: Int) {
        binding.searchProgressBar.isVisible = false
        binding.placeholderImage.setImageResource(R.drawable.ic_bad_connection)
        binding.placeholderMessage.text = getString(errorMsgResId)
        binding.placeholderImage.isVisible = true
        binding.placeholderMessage.isVisible = true
        binding.btnUpdate.isVisible = true
        binding.recyclerViewTracks.isVisible = false
        tracks.clear()
        updateTracksList(tracks)
    }

    private fun displayNothingFoundMessage() {
        binding.searchProgressBar.isVisible = false
        binding.placeholderImage.setImageResource(R.drawable.ic_placeholder_nothing_found)
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        binding.placeholderImage.isVisible = true
        binding.placeholderMessage.isVisible = true
        binding.btnUpdate.isVisible = false
        binding.recyclerViewTracks.isVisible = false
        tracks.clear()
        updateTracksList(tracks)
    }

    private fun loadHistory(historyTracks: ArrayList<Track>?) {
        binding.placeholderImage.isVisible = false
        binding.placeholderMessage.isVisible = false
        binding.btnUpdate.isVisible = false
        if (historyTracks.isNullOrEmpty()) {
            binding.recyclerViewTracks.isVisible=false
            binding.searchHistoryMessage.isVisible = false
            binding.btnClearHistory.isVisible = false
        } else {
            this.historyTracks = historyTracks
            historyAdapter.tracks = historyTracks.toCollection(ArrayList())
            binding.recyclerViewTracks.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
            binding.recyclerViewTracks.isVisible =true
            binding.searchHistoryMessage.isVisible = true
            binding.btnClearHistory.isVisible = true
        }
    }

    private fun updateHistory(historyTracks: ArrayList<Track>?) {
        if (historyTracks != null) {
            this.historyTracks = historyTracks
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





















