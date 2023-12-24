package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.artworkUrl100,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate.toString(),
            track.primaryGenreName,
            track.country,
            track.trackTimeMillis,
            track.previewUrl
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.id,
            track.artworkUrl100,
            track.trackName,
            track.trackTimeMillis,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}