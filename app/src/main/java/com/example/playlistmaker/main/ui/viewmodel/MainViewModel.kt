package com.example.playlistmaker.main.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator

class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(
                    this[ViewModelProvider
                        .AndroidViewModelFactory
                        .APPLICATION_KEY] as Application
                )
            }
        }
    }

    var isFirstTime: Boolean = true
    private val settingsInteractor = Creator.provideSettingsInteractor(application)

    fun setTheme() {
        if (isFirstTime) {
            settingsInteractor.setThemeToggle()
            isFirstTime = false
        }
    }
}