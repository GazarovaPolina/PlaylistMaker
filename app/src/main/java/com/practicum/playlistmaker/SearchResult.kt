package com.practicum.playlistmaker

sealed class SearchResult<T> {
    class Success<T>(val result: T): SearchResult<T>()
    class Failure<T>: SearchResult<T>()
}