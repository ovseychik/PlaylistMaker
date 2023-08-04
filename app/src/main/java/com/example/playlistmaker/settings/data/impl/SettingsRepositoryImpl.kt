package com.example.playlistmaker.settings.data.impl

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeToggle

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    companion object {
        private const val SETTINGS_PREFERENCES = "settings_preferences"
        private const val SWITCH_DARK_THEME = "switch_dark_theme"
    }

    val sharedPrefsSettings = context.getSharedPreferences(
        SETTINGS_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

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