package com.example.playlistmaker.search.data.devicestorage

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistorySharedPrefs(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : SearchHistory {
    companion object {
        const val SEARCH_HISTORY = "search_history_key"
    }

    override fun getSearchHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return ArrayList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun putSearchHistory(tracks: List<Track>) {
        sharedPrefs
            .edit()
            .putString(
                SEARCH_HISTORY,
                gson.toJson(tracks)
            )
            .apply()
    }

    override fun clearSearchHistory() {
        sharedPrefs.edit().clear().apply()
    }
}