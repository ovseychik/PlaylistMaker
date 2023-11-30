package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.model.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {
    private var timerJob: Job? = null
    private var isPlayerCreated = false

    private var _statePlayerLiveData = MutableLiveData<PlayerState>()
    fun statePlayerLiveData(): LiveData<PlayerState> = _statePlayerLiveData

    fun preparePlayer(url: String) {
        if (!isPlayerCreated) playerInteractor.preparePlayer(url) { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    _statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                    timerJob?.cancel()
                }

                else -> Unit
            }
        }
        isPlayerCreated = true
    }

    private fun startTimer(playerState: PlayerState) {
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.STATE_PLAYING) {
                delay(REFRESH_TRACK_PROGRESS_MILLIS)
                getCurrentPosition()
            }
        }
        if (playerState == PlayerState.STATE_PREPARED) {
            timerJob?.cancel()
        }
    }


    fun controlPlayerState() {
        playerInteractor.controlPlayerState { state ->
            when (state) {
                PlayerState.STATE_PLAYING -> {
                    getCurrentPosition()
                    startTimer(PlayerState.STATE_PLAYING)
                    _statePlayerLiveData.postValue(PlayerState.STATE_PLAYING)
                }

                PlayerState.STATE_PAUSED -> {
                    timerJob?.cancel()
                    _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
                }

                PlayerState.STATE_PREPARED -> {
                    timerJob?.cancel()
                    _statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                }

                PlayerState.STATE_DEFAULT -> {
                    timerJob?.cancel()
                    _statePlayerLiveData.postValue(PlayerState.STATE_DEFAULT)
                }

                PlayerState.STATE_IDLE -> {
                    timerJob?.cancel()
                    _statePlayerLiveData.postValue(PlayerState.STATE_IDLE)
                }
            }
        }
    }

    fun onPause() {
        timerJob?.cancel()
        _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
        playerInteractor.pausePlayer()
    }

    fun onDestroy() {
        timerJob?.cancel()
        playerInteractor.releasePlayer()
    }

    fun onResume() {
        timerJob?.cancel()
        _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        playerInteractor.releasePlayer()
    }

    fun getCurrentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    fun getCoverArtWork(url: String) = url.replaceAfterLast('/', "512x512bb.jpg")

    companion object {
        private const val REFRESH_TRACK_PROGRESS_MILLIS = 100L
    }
}