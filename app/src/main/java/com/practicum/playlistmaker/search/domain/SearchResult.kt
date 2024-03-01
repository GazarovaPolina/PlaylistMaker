package com.practicum.playlistmaker.search.domain

sealed class SearchResult<T> {
    class Success<T>(val result: T): SearchResult<T>()
    class Failure<T>(val message: String, val result: T? = null): SearchResult<T>()
}