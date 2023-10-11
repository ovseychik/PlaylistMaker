package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeToggle

class SettingsRepositoryImpl(private val sharedPrefsSettings: SharedPreferences) : SettingsRepository {
    companion object {
        const val SETTINGS_PREFERENCES = "settings_preferences"
        private const val SWITCH_DARK_THEME = "switch_dark_theme"
    }

    val darkTheme = sharedPrefsSettings.getBoolean(SWITCH_DARK_THEME, false)

    override fun getThemeToggle(): ThemeToggle {
        return ThemeToggle(darkTheme)
    }

    override fun setThemeToggle() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun updateThemeSettings(toggle: ThemeToggle) {
        AppCompatDelegate.setDefaultNightMode(
            if (toggle.darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPrefsSettings.edit()
            .putBoolean(SWITCH_DARK_THEME, toggle.darkTheme)
            .apply()
    }
}