package com.example.playlistmaker.search.data.devicestorage

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistory {
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}