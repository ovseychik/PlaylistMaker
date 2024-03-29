package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.library.data.db.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.TrackInPlaylistConverter
import com.example.playlistmaker.library.data.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.library.data.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.library.domain.db.PlaylistsRepository
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            searchHistory = get(),
            context = get(),
            appDatabase = get()
        )
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefsSettings = get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = get())
    }

    single {
        androidContext().getSharedPreferences(
            SettingsRepositoryImpl.SETTINGS_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    factory {
        MediaPlayer()
    }

    factory { TrackDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(appDatabase = get(), trackDbConverter = get())
    }


    factory { PlaylistDbConverter() }

    factory { TrackInPlaylistConverter() }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(
            appDatabase = get(),
            playlistDbConverter = get(),
            trackInPlaylistConverter = get(),
            imageStorage = get(),
        )
    }

}