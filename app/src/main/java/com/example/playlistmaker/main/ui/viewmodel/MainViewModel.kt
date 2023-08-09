package com.example.playlistmaker.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor

class MainViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    var isFirstTime: Boolean = true

    fun setTheme() {
        if (isFirstTime) {
            settingsInteractor.setThemeToggle()
            isFirstTime = false
        }
    }
}