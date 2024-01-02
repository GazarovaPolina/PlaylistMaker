package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val iTunesService: ITunesSearchApi, private val context: Context) : NetworkClient {

    override fun doRequest(dto: Any): Response {

        if (!isInternetConnect()) {
            return Response().apply { resultCode = -1 }
        }

        return if (dto is TracksSearchRequest) {
            val resp = iTunesService.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            body.apply {
                resultCode = resp.code()
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }

    private fun isInternetConnect(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null

    }
}