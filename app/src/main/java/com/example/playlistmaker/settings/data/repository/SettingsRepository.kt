package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.domain.model.ThemeToggle

interface SettingsRepository {
    fun getThemeToggle(): ThemeToggle
    fun setThemeToggle()
    fun updateThemeSettings(toggle: ThemeToggle)
}