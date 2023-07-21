package com.practicum.playlistmaker

import android.app.Activity
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var listener: OnSharedPreferenceChangeListener

    private var historyTracks = ArrayList<Track>()

    private val historyAdapter = TrackAdapter(historyTracks)

    private var query: String? = null

    private val iTunesSearchBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(iTunesSearchApi::class.java)

    private var tracks = ArrayList<Track>()

    private var adapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarIconOnClickListener()

        setIconClearOnClickListener()

        addSearchQueryChangedListener()

        executeSearchQuery()

        val sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        searchHistory(sharedPrefs)
        //sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).commit()
    }

    private fun setToolbarIconOnClickListener() {
        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setIconClearOnClickListener() {
        binding.iconClear.setOnClickListener {
            binding.editTextSearch.setText("")
            binding.placeholderMessage.visibility = View.GONE
            binding.placeholderImage.visibility = View.GONE
            binding.btnUpdate.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun addSearchQueryChangedListener() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClear.visibility = clearButtonVisibility(s)
                query = s?.toString()
                if (binding.iconClear.visibility == View.GONE) {
                    clearTracksList()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        binding.editTextSearch.addTextChangedListener(searchTextWatcher)
    }

    private fun executeSearchQuery() {


        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                adapter.tracks = tracks

                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerView.adapter = adapter
                search()
            }
            false
        }

        binding.btnUpdate.setOnClickListener {
            search()
        }
    }


    private fun search() {
        if (binding.editTextSearch.text.isNotEmpty()) {
            imdbService.search(binding.editTextSearch.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        val errorType = if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                            if (tracks.isEmpty()) ErrorType.NO_DATA else null
                        } else {
                            ErrorType.NETWORK_ERROR
                        }

                        updateOnError(errorType)
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        updateOnError(ErrorType.NETWORK_ERROR)
                    }
                })
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val query = savedInstanceState.getString(SEARCH_QUERY)
        binding.editTextSearch.setText(query)
        this.query = query
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearTracksList() {
        tracks.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, query)
    }

    companion object {
        private val SEARCH_QUERY = "SEARCH_QUERY"
    }

    private fun updateOnError(errorType: ErrorType?) {
        binding.placeholderImage.isVisible = errorType != null
        binding.placeholderMessage.isVisible = errorType != null
        binding.btnUpdate.isVisible = errorType == ErrorType.NETWORK_ERROR

        errorType ?: return

        tracks.clear()
        adapter.notifyDataSetChanged()

        binding.placeholderImage.setImageResource(errorType.iconResId)
        binding.placeholderMessage.setText(errorType.textResId)
    }


    private fun searchHistory(sharedPrefs: SharedPreferences) {

        adapter.onItemClick = {

            var searchHistoryTracks = ArrayList<Track>()

            if (sharedPrefs.contains(SEARCH_HISTORY_KEY)) {

                if (read(sharedPrefs).isNotEmpty()) {
                    searchHistoryTracks.addAll(read(sharedPrefs))

                    for (track in searchHistoryTracks) {

                        if (track.trackId == it.trackId) {
                            searchHistoryTracks.remove(track)
                            break
                        }
                    }

                    if (searchHistoryTracks.count() > 9) searchHistoryTracks.removeLast()
                }
            }

            searchHistoryTracks.add(0, it)
            write(sharedPrefs, searchHistoryTracks)

        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.editTextSearch.hasFocus() && p0?.isEmpty() == true && read(sharedPrefs).isNotEmpty()) {
                    fillTracksList(sharedPrefs)
                    binding.searchHistoryGroup.visibility = View.VISIBLE
                } else {
                    historyTracks.clear()
                    historyAdapter.notifyDataSetChanged()
                    binding.searchHistoryGroup.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editTextSearch.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus && binding.editTextSearch.text.isEmpty() && read(sharedPrefs).isNotEmpty()) {
                fillTracksList(sharedPrefs)
                binding.searchHistoryGroup.visibility = View.VISIBLE
            } else {
                binding.searchHistoryGroup.visibility = View.GONE
            }
        }

    }

    private fun fillTracksList(sharedPrefs: SharedPreferences) {
        historyAdapter.tracks = historyTracks
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = historyAdapter
        historyTracks.addAll(read(sharedPrefs))
    }

    private fun read(sharedPrefs: SharedPreferences): Array<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    private fun write(sharedPrefs: SharedPreferences, tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)

        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}


enum class ErrorType(@StringRes val textResId: Int, @DrawableRes val iconResId: Int) {
    NO_DATA(R.string.nothing_found, R.drawable.ic_placeholder_nothing_found),
    NETWORK_ERROR(R.string.bad_connection, R.drawable.ic_bad_connection)
}