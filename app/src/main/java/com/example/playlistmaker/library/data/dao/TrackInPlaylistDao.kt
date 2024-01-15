package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.entity.TrackInPlaylist

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylist::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackInPlaylist: TrackInPlaylist)

    @Query("SELECT * FROM track_in_playlist")
    suspend fun getTracksFromPlaylists(): List<TrackInPlaylist>?

    @Query("DELETE FROM track_in_playlist WHERE track_id = :id")
    suspend fun deleteTrackByIdFromTracksInPlaylists(id: Int)
}