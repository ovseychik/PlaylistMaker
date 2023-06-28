package com.example.playlistmaker.domain.api.player

import com.example.playlistmaker.domain.PlayerState

interface PlayerRepository {
    fun preparePlayer(url: String, onStateChangedTo: (s: PlayerState) -> Unit)
    fun startPlayer()
    fun getCurrentPosition(): Int
    fun pausePlayer()
    fun releasePLayer()
    fun controlPlayerState(onStateChangedTo: (s: PlayerState) -> Unit)
    fun destroy()
}