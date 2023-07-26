package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.data.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeToggle

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getThemeToggle(): ThemeToggle = repository.getThemeToggle()

    override fun setThemeToggle() = repository.setThemeToggle()

    override fun updateThemeSettings(toggle: ThemeToggle) = repository.updateThemeSettings(toggle)
}