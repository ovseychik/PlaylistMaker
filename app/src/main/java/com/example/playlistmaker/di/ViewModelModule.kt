package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.viewmodel.FavoritePlaylistsFragmentViewModel
import com.example.playlistmaker.library.ui.viewmodel.FavoriteTracksFragmentViewModel
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(settingsInteractor = get())
    }

    viewModel {
        PlayerViewModel(playerInteractor = get(), favoritesInteractor = get())
    }

    viewModel {
        SearchViewModel(searchInteractor = get())
    }

    viewModel {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }

    viewModel {
        FavoritePlaylistsFragmentViewModel()
    }

    viewModel {
        FavoriteTracksFragmentViewModel(favoritesInteractor = get())
    }
}