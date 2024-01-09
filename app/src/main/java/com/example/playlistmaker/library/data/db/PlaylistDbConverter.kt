package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    fun map(playlistEntity: PlaylistEntity): Playlist {
        val trackIdsList =
            if (playlistEntity.trackIds != "null" && !playlistEntity.trackIds.isNullOrEmpty()) {
                fromString(playlistEntity.trackIds)
            } else {
                emptyList()
            }

        return Playlist(
            id = playlistEntity.id,
            playlistTitle = playlistEntity.playlistTitle,
            playlistDescription = playlistEntity.playlistDescription,
            playlistCoverPath = playlistEntity.playlistCoverPath,
            trackIds = trackIdsList,
            numberOfTracks = playlistEntity.numberOfTracks ?: 0,
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            playlistTitle = playlist.playlistTitle,
            playlistDescription = playlist.playlistDescription,
            playlistCoverPath = playlist.playlistCoverPath,
            trackIds = fromList(playlist.trackIds),
            numberOfTracks = playlist.trackIds?.size
        )
    }

    private fun fromString(value: String?): List<String>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    private fun fromList(list: List<String>?): String? {
        if (list == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(list)
    }
}