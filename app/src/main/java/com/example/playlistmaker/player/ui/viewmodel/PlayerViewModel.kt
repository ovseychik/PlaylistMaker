package com.example.playlistmaker.player.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val REFRESH_TRACK_PROGRESS = 100L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val playerInteractor = Creator.providePlayerInteractor()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val runnable = postCurrentTimeControl()

    private var statePlayerLiveData = MutableLiveData(PlayerState.STATE_PREPARED)

    fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData

    private var currentTimerLiveData = MutableLiveData(0)

    fun getCurrentTimerLiveData(): LiveData<Int> = currentTimerLiveData


    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url) { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                    mainThreadHandler.removeCallbacks(runnable)
                }
                else -> Unit
            }
        }
    }

    fun postCurrentTimeControl(): Runnable {
        return object : Runnable {
            override fun run() {
                var currentTimerPosition = playerInteractor.getCurrentPosition()
                mainThreadHandler.postDelayed(this, REFRESH_TRACK_PROGRESS)
                currentTimerLiveData.postValue(currentTimerPosition)
            }

        }
    }


    fun changePlayerState() {
        playerInteractor.controlPlayerState { state ->
            when (state) {
                PlayerState.STATE_PLAYING -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    mainThreadHandler.post(runnable)
                    statePlayerLiveData.postValue(PlayerState.STATE_PLAYING)
                }
                PlayerState.STATE_PAUSED -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
                }
                PlayerState.STATE_PREPARED -> {
                    mainThreadHandler.removeCallbacks(runnable)
                    mainThreadHandler.post(runnable)
                    statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
                }

                else -> Unit
            }
        }
    }

    fun onPause() {
        mainThreadHandler.removeCallbacks(runnable)
        statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
        playerInteractor.pausePlayer()

    }

    fun onDestroy() {
        mainThreadHandler.removeCallbacks(runnable)

    }

    fun onResume() {
        mainThreadHandler.removeCallbacks(runnable)
        statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)

    }

}