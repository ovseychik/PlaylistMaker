package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val APP_PREFERENCES = "app_preferences"
const val DARK_THEME = "dark_theme"

class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        val sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, false)

        PermissionRequester.initialize(applicationContext)
    }

}