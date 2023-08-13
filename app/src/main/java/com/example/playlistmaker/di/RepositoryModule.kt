package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get(), searchHistory = get())
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
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
}