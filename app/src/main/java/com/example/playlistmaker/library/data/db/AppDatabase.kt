package com.example.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.dao.PlaylistDao
import com.example.playlistmaker.library.data.dao.TrackDao
import com.example.playlistmaker.library.data.dao.TrackInPlaylistDao
import com.example.playlistmaker.library.data.entity.PlaylistEntity
import com.example.playlistmaker.library.data.entity.TrackEntity
import com.example.playlistmaker.library.data.entity.TrackInPlaylist

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylist::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

    companion object {
        const val DB_NAME = "playlistmaker-database.db"
    }

}