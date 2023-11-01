package com.example.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.model.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val runnable = postCurrentTimeControl()
    private var isPlayerCreated = false

    private var _statePlayerLiveData = MutableLiveData<PlayerState>()
    fun statePlayerLiveData(): LiveData<PlayerState> = _statePlayerLiveData

    fun preparePlayer(url: String) {
        if (!isPlayerCreated) playerInteractor.preparePlayer(url) { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    _statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                    mainThreadHandler.removeCallbacks(runnable)
                }

                else -> Unit
            }
        }
        isPlayerCreated = true
    }

    private fun postCurrentTimeControl(): Runnable {
        return object : Runnable {
            override fun run() {
                getCurrentPosition()
                _statePlayerLiveData.postValue(PlayerState.STATE_PLAYING)
                mainThreadHandler.postDelayed(this, REFRESH_TRACK_PROGRESS_MILLIS)
            }
        }
    }

    fun controlPlayerState() {
        playerInteractor.controlPlayerState { state ->
            when (state) {
                PlayerState.STATE_PLAYING -> {
                    getCurrentPosition()
                    mainThreadHandler.post(runnable)
                    _statePlayerLiveData.postValue(PlayerState.STATE_PLAYING)
                }

                PlayerState.STATE_PAUSED -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
                }

                PlayerState.STATE_PREPARED -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    _statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                }

                PlayerState.STATE_DEFAULT -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    _statePlayerLiveData.postValue(PlayerState.STATE_DEFAULT)
                }

                PlayerState.STATE_IDLE -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    _statePlayerLiveData.postValue(PlayerState.STATE_IDLE)
                }
            }
        }
    }

    fun onPause() {
        mainThreadHandler.removeCallbacks(runnable)
        _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun onDestroy() {
        mainThreadHandler.removeCallbacks(runnable)
        playerInteractor.releasePlayer()
    }

    fun onResume() {
        mainThreadHandler.removeCallbacks(runnable)
        _statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler.removeCallbacks(runnable)
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