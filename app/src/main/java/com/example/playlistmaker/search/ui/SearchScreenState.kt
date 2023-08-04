package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchScreenState {
    object Loading : SearchScreenState
    data class Success(val data: List<Track>) : SearchScreenState
    data class History(val history: List<Track>) : SearchScreenState
    data class Error(val message: String) : SearchScreenState
    data class Empty(val message: String) : SearchScreenState
}