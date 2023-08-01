package com.example.playlistmaker.search.data.device_storage

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistory {
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}