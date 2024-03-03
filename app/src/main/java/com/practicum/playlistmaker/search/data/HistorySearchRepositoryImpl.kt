package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.search.domain.HistorySearchRepository
import com.practicum.playlistmaker.search.domain.models.Track


class HistorySearchRepositoryImpl(private val sharedPrefs: SharedPreferences, private val gson: Gson) : HistorySearchRepository {

    override fun addTrackToHistory(track: Track) {
        val searchHistoryTracks = ArrayList<Track>()

        if (sharedPrefs.contains(SEARCH_HISTORY_KEY)) {

            if (getTracksFromHistory().isNotEmpty()) {
                searchHistoryTracks.addAll(getTracksFromHistory())

                for (trackFromHistory in searchHistoryTracks) {

                    if (trackFromHistory.trackId == track.trackId) {
                        searchHistoryTracks.remove(trackFromHistory)
                        break
                    }
                }

                if (searchHistoryTracks.count() > 9) searchHistoryTracks.removeLast()
            }
        } else {
            val json = gson.toJson(searchHistoryTracks)
            sharedPrefs.edit {
                putString(SEARCH_HISTORY_KEY, json)
            }
        }

        searchHistoryTracks.add(0, track)
        write(sharedPrefs, searchHistoryTracks)
    }

    override fun getTracksFromHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    override fun clearSearchHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun write(sharedPrefs: SharedPreferences, tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)

        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}