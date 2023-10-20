package com.practicum.playlistmaker

sealed class SearchResult<T>(val result: T? = null, val errorMsg: String? = null) {
    class Success<T>(result: T): SearchResult<T>(result)
    class Failure<T>(errorMsg: String, result: T? = null): SearchResult<T>(result, errorMsg)
}