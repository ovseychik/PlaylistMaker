package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.model.ThemeToggle

interface SettingsInteractor {
    fun getThemeToggle(): ThemeToggle
    fun setThemeToggle()
    fun updateThemeSettings(toggle: ThemeToggle)
}