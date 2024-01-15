package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.library.data.entity.TracksToPlaylistLinkEntity

// Для ухода от хранения треков в json формате. Будет many-to-many связывающая таблица

@Dao
interface TracksToPlaylistLinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackToPlaylistLink: TracksToPlaylistLinkEntity)

    @Query("SELECT * FROM track_to_playlist_link")
    suspend fun getAll(): List<TracksToPlaylistLinkEntity>

    @Query("SELECT * FROM track_to_playlist_link WHERE track_id = :trackId AND playlist_id = :playlistId")
    suspend fun get(trackId: Int, playlistId: Int): TracksToPlaylistLinkEntity?

    @Update
    suspend fun update(trackToPlaylistLink: TracksToPlaylistLinkEntity)

    @Delete
    suspend fun delete(trackToPlaylistLink: TracksToPlaylistLinkEntity)

}