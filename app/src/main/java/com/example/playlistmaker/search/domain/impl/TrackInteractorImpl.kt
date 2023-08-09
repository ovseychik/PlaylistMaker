package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

//TODO: rename class to SearchInteractorImpl during hiatus
class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTrack(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTrack(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun putSearchHistory(tracks: List<Track>) {
        return repository.putSearchHistory(tracks)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }

}