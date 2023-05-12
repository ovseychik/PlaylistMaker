package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

private const val HISTORY_KEY = "HISTORY_KEY"

class SearchHistory(private val sharedPrefs: SharedPreferences, private val gson: Gson) {//

    fun addTrack(track: Track){
        val listString = getListString()
        if(listString == null){
            saveList(listOf(track))
            return
        }
        val list = listString.toTrackList()
        if (list.contains(track)){
            list.remove(track)
        }
        if (list.size >= 10){
            list.removeAt(list.size - 1)
        }
        list.add(0, track)
        saveList(list)
    }

    fun removeTrack(track: Track){
        val list = getListString()?.toTrackList() ?: return
        list.remove(track)
        saveList(list)
        if (list.isEmpty()){
            clearHistory()
        }
    }

    fun clearHistory(){
        sharedPrefs.edit().remove(HISTORY_KEY).apply()
    }

    fun getHistory() : List<Track> = getListString()?.toTrackList() ?: emptyList()

    private fun getListString() : String? = sharedPrefs.getString(HISTORY_KEY,null)

    private fun saveList(list: List<Track>) = sharedPrefs.edit().putString(HISTORY_KEY, gson.toJson(list)).apply()

    private fun String.toTrackList() = gson.fromJson<ArrayList<Track>>(this, object : TypeToken<ArrayList<Track>>() {}.type)
}