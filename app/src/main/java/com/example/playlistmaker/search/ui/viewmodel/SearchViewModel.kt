package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchScreenState


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val MAX_SEARCH_HISTORY = 10
        private val SEARCH_REQUEST_TOKEN = Any()


        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private var trackSearchHistory = ArrayList<Track>()
    private var recentSearchExpression: String? = null
    private val tracksInteractor = Creator.provideTrackInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())

    // Состояние поиска
    private val _searchStateLiveData = MutableLiveData<SearchScreenState>()
    val searchStateLiveData: LiveData<SearchScreenState> = _searchStateLiveData

    // Состояние истории
    private val _historyLiveData = MutableLiveData<ArrayList<Track>>()
    val historyLiveData: LiveData<ArrayList<Track>> = _historyLiveData
    fun stateObserver(): LiveData<SearchScreenState> = mediatorStateLiveData

    init {
        trackSearchHistory.addAll(tracksInteractor.getSearchHistory())
        _historyLiveData.postValue(trackSearchHistory)
    }

    private fun renderState(state: SearchScreenState) {
        _searchStateLiveData.postValue(state)
    }

    private val mediatorStateLiveData = MediatorLiveData<SearchScreenState>().also { liveData ->
        liveData.addSource(_searchStateLiveData) { screenState ->
            liveData.value = when (screenState) {
                is SearchScreenState.Success -> SearchScreenState.Success(screenState.data)
                is SearchScreenState.Loading -> screenState
                is SearchScreenState.Error -> screenState
                is SearchScreenState.Empty -> screenState
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tracksInteractor.putSearchHistory(trackSearchHistory)
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun putTrackToHistory(track: Track) {
        trackSearchHistory = tracksInteractor.getSearchHistory() as ArrayList<Track>
        trackSearchHistory.remove(track)
        if (trackSearchHistory.size == MAX_SEARCH_HISTORY) {
            trackSearchHistory.removeAt(trackSearchHistory.size - 1)
        }
        trackSearchHistory.add(0, track)
        tracksInteractor.putSearchHistory(trackSearchHistory)
        _historyLiveData.postValue(trackSearchHistory)
    }

    fun searchDebounce(changedText: String) {
        if (recentSearchExpression == changedText) {
            return
        }

        this.recentSearchExpression = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun clearHistory() {
        tracksInteractor.clearSearchHistory()
        trackSearchHistory.clear()
        renderState(
            SearchScreenState.Success(
                data = trackSearchHistory,
            )
        )
    }

    fun fillHistory() {
        trackSearchHistory.clear()
        trackSearchHistory.addAll(tracksInteractor.getSearchHistory())
        _historyLiveData.postValue(trackSearchHistory)
    }

    fun searchTrack(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)

            tracksInteractor.searchTrack(newSearchText, object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val trackList = mutableListOf<Track>()
                    if (foundTracks != null) {
                        trackList.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SearchScreenState.Error(
                                    message = getApplication<Application>().getString(R.string.check_network),
                                )
                            )
                        }

                        trackList.isEmpty() -> {
                            renderState(
                                SearchScreenState.Empty(
                                    message = getApplication<Application>().getString(R.string.nothing_found),
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

}
