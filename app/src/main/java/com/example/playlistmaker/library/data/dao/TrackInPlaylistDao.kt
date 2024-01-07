package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.library.data.entity.TrackInPlaylist

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylist::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackInPlaylist: TrackInPlaylist)
}