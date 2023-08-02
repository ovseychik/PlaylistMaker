package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.network.TrackRequest
import com.example.playlistmaker.search.data.device_storage.SearchHistory
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.repository.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.network.TrackResponse
import com.example.playlistmaker.util.Resource

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : TrackRepository {
    companion object{
        private const val ERROR_NO_NETWORK = 0
        private const val SEARCH_SUCCESS = 200
    }
    override fun searchTrack(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(expression))
        return when (response.resultCode) {
            ERROR_NO_NETWORK -> {
                Resource.Error(R.string.check_network.toString())
            }

            SEARCH_SUCCESS -> {
                Resource.Success((response as TrackResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistory.getSearchHistory().map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl,
            )
        }
    }

    override fun putSearchHistory(tracks: List<Track>) {
        searchHistory.putSearchHistory(tracks as List<Track>)
    }

    override fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }

}