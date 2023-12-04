package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.SearchResult
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTrack(expression: String): Flow<SearchResult<List<Track>?, String?>>
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}