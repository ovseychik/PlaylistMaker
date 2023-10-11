package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeToggle
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {
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