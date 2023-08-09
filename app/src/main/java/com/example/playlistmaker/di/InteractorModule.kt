package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.model.PlayerInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import org.koin.dsl.module

val interactorModule = module {
    factory <PlayerInteractor> {
        PlayerInteractorImpl(playerRepository = get())
    }

/*    single<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }*/

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
}