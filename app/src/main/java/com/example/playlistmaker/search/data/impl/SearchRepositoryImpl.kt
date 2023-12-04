package com.example.playlistmaker.search.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.network.TrackRequest
import com.example.playlistmaker.search.data.devicestorage.SearchHistory
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.network.TrackResponse
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory,
    private val context: Context
) : SearchRepository {
    companion object {
        private const val ERROR_NO_NETWORK = 0
        private const val SEARCH_SUCCESS = 200
    }

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            ERROR_NO_NETWORK -> {
                emit(Resource.Error(R.string.check_network.toString()))
            }

            SEARCH_SUCCESS -> {
                with(response as TrackResponse) {
                    val data = results.map {
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
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error(R.string.error_server_error.toString()))
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