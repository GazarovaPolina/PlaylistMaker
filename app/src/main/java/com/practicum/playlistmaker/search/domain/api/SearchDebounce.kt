package com.practicum.playlistmaker.search.domain.api

interface SearchDebounce {

    fun searchDebounce(request: () -> Unit)

    fun clear()
}