package com.example.playlistmaker.library.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Для ухода от хранения треков в json формате. Будет many-to-many связывающая таблица
@Entity(
    tableName = "track_to_playlist_link",
    primaryKeys = ["track_id", "playlist_id"],
    foreignKeys = [
        ForeignKey(
            entity = TrackInPlaylist::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["track_id"]),
        Index(value = ["playlist_id"])
    ]
)
data class TracksToPlaylistLinkEntity(
    @ColumnInfo(name = "track_id")
    val trackId: Int,
    @ColumnInfo(name = "playlist_id")
    val playlistId: Int,
)