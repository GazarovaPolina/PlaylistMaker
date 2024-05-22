package com.practicum.playlistmaker.player.ui

class TracksCountMessageEndingChanger() {

    fun getTracksCountMessageEnding(tracksCount: Int): String {

         if (tracksCount % 100 in 11..14) {
             return "$tracksCount треков"
         }

        return when (tracksCount % 10 ) {
            1 -> "$tracksCount трек"
            in 2..4 -> "$tracksCount трека"
            else -> "$tracksCount треков"
        }
    }
}