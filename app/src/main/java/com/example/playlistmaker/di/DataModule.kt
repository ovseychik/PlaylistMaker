package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.search.data.device_storage.SearchHistory
import com.example.playlistmaker.search.data.device_storage.SearchHistorySharedPrefs
import com.example.playlistmaker.search.data.device_storage.SearchHistorySharedPrefs.Companion.SEARCH_HISTORY
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val iTunesUrl = "https://itunes.apple.com"

val dataModule = module {
    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(iTunesUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<SearchHistory> {
        SearchHistorySharedPrefs(sharedPrefs = get(), gson = get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(iTunesService = get(), androidContext())
    }

}