package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val searchInteractor: SearchInteractor
) :
    AndroidViewModel(application) {
    private var trackSearchHistory = ArrayList<Track>()
    private var recentSearchExpression: String? = null

    //private val handler = Handler(Looper.getMainLooper())
    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_MILLIS, viewModelScope, true) { changedText ->
            searchTrack(changedText)
        }

    private val _searchStateLiveData = MutableLiveData<SearchScreenState>()
    val searchStateLiveData: LiveData<SearchScreenState> = _searchStateLiveData

    fun stateObserver(): LiveData<SearchScreenState> = mediatorStateLiveData

    val trackList: MutableLiveData<ArrayList<Track>> = MutableLiveData(ArrayList())

    init {
        trackSearchHistory.addAll(searchInteractor.getSearchHistory())
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
        searchInteractor.putSearchHistory(trackSearchHistory)
        //handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun putTrackToHistory(track: Track) {
        trackSearchHistory = searchInteractor.getSearchHistory() as ArrayList<Track>
        trackSearchHistory.remove(track)
        if (trackSearchHistory.size == MAX_SEARCH_HISTORY) {
            trackSearchHistory.removeAt(trackSearchHistory.size - 1)
        }
        trackSearchHistory.add(0, track)
        searchInteractor.putSearchHistory(trackSearchHistory)
    }

    fun searchDebounce(changedText: String) {
        if (recentSearchExpression != changedText) {
            recentSearchExpression = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun clearHistory() {
        searchInteractor.clearSearchHistory()
        trackSearchHistory.clear()
        renderState(
            SearchScreenState.Success(
                data = trackSearchHistory
            )
        )
    }

    fun fillHistory() {
        trackSearchHistory.clear()
        trackSearchHistory.addAll(searchInteractor.getSearchHistory())
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

            viewModelScope.launch {
                searchInteractor
                    .searchTrack(newSearchText)
                    .collect { pair ->
                        processResult(pair.resultList, pair.error)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val trackList = mutableListOf<Track>()
        if (foundTracks != null) {
            trackList.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    SearchScreenState.Error(
                        message = application.getString(
                            R.string.error_server_error
                        )
                    )
                )
            }

            trackList.isEmpty() -> {
                renderState(
                    SearchScreenState.Empty(
                        message = application.getString(R.string.nothing_found)
                    )
                )
            }

            else -> {
                renderState(SearchScreenState.Success(trackList))
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2_000L
        private const val MAX_SEARCH_HISTORY = 10
    }
}
