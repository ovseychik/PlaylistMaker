package com.example.playlistmaker.main.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor

class MainViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    var isFirstTime: Boolean = true

    private val playlistLiveData = MutableLiveData<Playlist>()

    fun setTheme() {
        if (isFirstTime) {
            settingsInteractor.setThemeToggle()
            isFirstTime = false
        }
    }

    fun setPlaylist(playlist: Playlist) {
        playlistLiveData.postValue(playlist)
    }

    fun getPlaylist(): LiveData<Playlist> {
        return playlistLiveData
    }

}