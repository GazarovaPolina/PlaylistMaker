package com.practicum.playlistmaker.player.ui

import android.util.Log

class CountMessageEndingChanger() {

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

    fun getMinutesMessageEnding(minutesCount: Long): String {
        return when {
            minutesCount % 10 == 1L -> "$minutesCount минута"
            minutesCount % 100 in 11L..14L -> "$minutesCount минут"
            minutesCount % 10 in 2L..4L -> "$minutesCount минуты"
            else -> "$minutesCount минут"
        }
    }
}