package com.practicum.playlistmaker

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var query: String? = null

    private lateinit var searchEditText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var btnUpdate: Button

    private val iTunesSearchBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val IMDbService = retrofit.create(iTunesSearchApi::class.java)

    private lateinit var tracksList: RecyclerView

    private val tracks = ArrayList<Track>()

    private var adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarSearch = findViewById<Toolbar>(R.id.toolbarSearch)

        toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.editTextSearch)
        val clearButton = findViewById<ImageView>(R.id.iconClear)

        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                query = s?.toString()
                if (clearButton.visibility == View.GONE) {
                    clearTracksList()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        tracksList = findViewById(R.id.recyclerView)
        adapter.tracks = tracks

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        btnUpdate = findViewById(R.id.btn_update)

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        btnUpdate.setOnClickListener {
            search()
        }
    }

    private fun search() {
        if (searchEditText.text.isNotEmpty()) {
            IMDbService.search(searchEditText.text.toString())
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
        searchEditText.setText(query)
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
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    private fun updateOnError(errorType: ErrorType?) {
        placeholderImage.isVisible = errorType != null
        placeholderMessage.isVisible = errorType != null
        btnUpdate.isVisible = errorType == ErrorType.NETWORK_ERROR

        errorType ?: return

        tracks.clear()
        adapter.notifyDataSetChanged()

        placeholderImage.setImageResource(errorType.iconResId)
        placeholderMessage.setText(errorType.textResId)
    }
}


enum class ErrorType(@StringRes val textResId: Int, @DrawableRes val iconResId: Int) {
    NO_DATA(R.string.nothing_found, R.drawable.ic_placeholder_nothing_found),
    NETWORK_ERROR(R.string.bad_connection, R.drawable.ic_bad_connection)
}