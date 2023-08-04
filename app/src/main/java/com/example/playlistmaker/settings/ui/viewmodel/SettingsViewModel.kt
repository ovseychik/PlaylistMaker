package com.example.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeToggle

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val sharingInteractor = Creator.provideSharingInteractor(getApplication())
    private val settingsInteractor = Creator.provideSettingsInteractor(getApplication())

    private val _themeSettingsLiveData = MutableLiveData<ThemeToggle>()
    val themeSettingsLiveData: LiveData<ThemeToggle> = _themeSettingsLiveData

    init {
        _themeSettingsLiveData.postValue(settingsInteractor.getThemeToggle())
    }

    fun updateThemeSettings(darkThemeEnabled: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeToggle(darkThemeEnabled))
        _themeSettingsLiveData.postValue(ThemeToggle(darkThemeEnabled))
    }

    fun shareApp(urlCourse: String) {
        sharingInteractor.shareApp(urlCourse)
    }

    fun openLegal(urlLegal: String) {
        sharingInteractor.openLegal(urlLegal)
    }

    fun contactSupport(email: String, subject: String, body: String) {
        sharingInteractor.contactSupport(email, subject, body)
    }

}