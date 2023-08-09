package com.example.playlistmaker.di

import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(settingsInteractor = get())
    }

    viewModel {
        PlayerViewModel(playerInteractor = get())
    }

    viewModel {
        SearchViewModel(application = androidApplication(), trackInteractor = get())
    }

    viewModel {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }
}