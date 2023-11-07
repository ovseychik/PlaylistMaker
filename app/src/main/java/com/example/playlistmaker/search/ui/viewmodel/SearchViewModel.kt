package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchScreenState

class SearchViewModel(application: Application, private val trackInteractor: SearchInteractor) :
    AndroidViewModel(application) {
    private var trackSearchHistory = ArrayList<Track>()
    private var recentSearchExpression: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _searchStateLiveData = MutableLiveData<SearchScreenState>()
    val searchStateLiveData: LiveData<SearchScreenState> = _searchStateLiveData

    fun stateObserver(): LiveData<SearchScreenState> = mediatorStateLiveData

    val trackList: MutableLiveData<ArrayList<Track>> = MutableLiveData(ArrayList())

    init {
        trackSearchHistory.addAll(trackInteractor.getSearchHistory())
    }

    private fun renderState(state: SearchScreenState) {
        _searchStateLiveData.postValue(state)
    }

    private val mediatorStateLiveData = MediatorLiveData<SearchScreenState>().also { liveData ->
        liveData.addSource(_searchStateLiveData) { screenState ->
            liveData.value = when (screenState) {
                is SearchScreenState.Success -> SearchScreenState.Success(screenState.data)
                is SearchScreenState.History -> SearchScreenState.History(screenState.history)
                is SearchScreenState.Loading -> screenState
                is SearchScreenState.Error -> screenState
                is SearchScreenState.Empty -> screenState
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackInteractor.putSearchHistory(trackSearchHistory)
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun putTrackToHistory(track: Track) {
        trackSearchHistory = trackInteractor.getSearchHistory() as ArrayList<Track>
        trackSearchHistory.remove(track)
        if (trackSearchHistory.size == MAX_SEARCH_HISTORY) {
            trackSearchHistory.removeAt(trackSearchHistory.size - 1)
        }
        trackSearchHistory.add(0, track)
        trackInteractor.putSearchHistory(trackSearchHistory)
    }

    fun searchDebounce(changedText: String) {
        if (recentSearchExpression == changedText) {
            return
        }

        this.recentSearchExpression = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_MILLIS
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun clearHistory() {
        trackInteractor.clearSearchHistory()
        trackSearchHistory.clear()
        renderState(
            SearchScreenState.Success(
                data = trackSearchHistory
            )
        )
    }

    fun fillHistory() {
        trackSearchHistory.clear()
        trackSearchHistory.addAll(trackInteractor.getSearchHistory())
        renderState(
            SearchScreenState.History(
                history = trackSearchHistory
            )
        )
    }

    fun updateTrackList(newTrackList: ArrayList<Track>) {
        trackList.value = newTrackList
    }

    fun clearTrackList() {
        trackList.value = ArrayList()
    }

    fun searchTrack(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)

            trackInteractor.searchTrack(newSearchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val trackList = mutableListOf<Track>()
                    if (foundTracks != null) {
                        trackList.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SearchScreenState.Error(
                                    message = R.string.check_network.toString()
                                )
                            )
                        }

                        trackList.isEmpty() -> {
                            renderState(
                                SearchScreenState.Empty(
                                    message = R.string.nothing_found.toString()
                                )
                            )
                        }

                        else -> {
                            renderState(
                                SearchScreenState.Success(
                                    data = trackList,
                                )
                            )
                        }
                    }
                }
            })
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2_000L
        private const val MAX_SEARCH_HISTORY = 10
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
