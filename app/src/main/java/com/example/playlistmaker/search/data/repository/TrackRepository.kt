package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource

interface TrackRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}