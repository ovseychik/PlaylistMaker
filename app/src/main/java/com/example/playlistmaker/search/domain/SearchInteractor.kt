package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface SearchInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)
    fun getSearchHistory(): List<Track>
    fun putSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, error: String?)
    }
}