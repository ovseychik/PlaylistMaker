package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.search.data.devicestorage.SearchHistory
import com.example.playlistmaker.search.data.devicestorage.SearchHistorySharedPrefs
import com.example.playlistmaker.search.data.devicestorage.SearchHistorySharedPrefs.Companion.SEARCH_HISTORY
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
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "playlistmaker-database.db")
            .build()
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