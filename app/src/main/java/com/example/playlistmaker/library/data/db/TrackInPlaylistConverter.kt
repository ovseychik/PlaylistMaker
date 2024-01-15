package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.entity.TrackInPlaylist
import com.example.playlistmaker.search.domain.model.Track

class TrackInPlaylistConverter {
    fun map(track: Track): TrackInPlaylist {
        return TrackInPlaylist(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }

    fun map(trackInPlaylist: TrackInPlaylist): Track {
        return Track(
            trackInPlaylist.id,
            trackInPlaylist.trackName.toString(),
            trackInPlaylist.artistName.toString(),
            trackInPlaylist.trackTimeMillis!!,
            trackInPlaylist.artworkUrl100.toString(),
            trackInPlaylist.collectionName.toString(),
            trackInPlaylist.releaseDate,
            trackInPlaylist.primaryGenreName.toString(),
            trackInPlaylist.country.toString(),
            trackInPlaylist.previewUrl,
        )
    }

}