package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.domain.model.QueryHandlingResult
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Int): Playlist
    suspend fun addTrackToPlaylist(playlistId: Int, track: Track)
    fun saveImageToPrivateStorage(uriFile: String?): String?
    fun getTracksFromPlaylistByIds(trackIds: List<String>): Flow<List<Track>>
    suspend fun getFlowPlaylistById(id: Int): Flow<Playlist?>
    suspend fun deletePlaylistById(playlistId: Int): Flow<QueryHandlingResult>
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int): Flow<List<Track>?>?
}