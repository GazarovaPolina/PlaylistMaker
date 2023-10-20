package com.practicum.playlistmaker.search.data

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.practicum.playlistmaker.search.domain.api.SearchDebounce

class SearchDebounceImpl : SearchDebounce {

    private val handler = Handler(Looper.getMainLooper())

    override fun searchDebounce(request: () -> Unit) {
        val searchRunnable = Runnable { request() }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST,
            postTime,
        )
    }

    override fun clear() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST)
    }

    companion object {
        private val SEARCH_REQUEST = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}