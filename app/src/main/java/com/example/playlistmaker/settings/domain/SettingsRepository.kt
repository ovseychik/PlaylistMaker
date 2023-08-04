package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.domain.model.ThemeToggle

interface SettingsRepository {
    fun getThemeToggle(): ThemeToggle
    fun setThemeToggle()
    fun updateThemeSettings(toggle: ThemeToggle)
}