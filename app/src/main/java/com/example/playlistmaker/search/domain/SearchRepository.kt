package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}