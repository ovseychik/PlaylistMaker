package com.example.playlistmaker.library.ui

import com.example.playlistmaker.search.domain.model.Track

sealed class FavoritesScreenState {
    data class Content(val tracks: List<Track>) : FavoritesScreenState()

    data class Empty(val emptyMessageRes: Int) : FavoritesScreenState()
}