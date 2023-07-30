package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.player.PlayerInteractor
import com.example.playlistmaker.domain.api.player.PlayerRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}