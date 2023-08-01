package com.example.playlistmaker.search.data.device_storage

import android.content.Context
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistorySharedPrefs(context: Context) : SearchHistory {
    companion object {
        private const val SEARCH_HISTORY = "search_history_key"
    }

    val sharedPrefs = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    override fun getSearchHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, "") ?: return ArrayList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun putSearchHistory(tracks: List<Track>) {
        sharedPrefs
            .edit()
            .putString(
                SEARCH_HISTORY,
                Gson().toJson(tracks)
            )
            .apply()
    }

    override fun clearSearchHistory() {
        sharedPrefs.edit().clear().apply()
    }
}