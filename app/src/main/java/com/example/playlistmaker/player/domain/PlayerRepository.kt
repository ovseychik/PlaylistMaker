package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {
    fun preparePlayer(url: String, onStateChangedTo: (s: PlayerState) -> Unit)
    fun startPlayer()
    fun getCurrentPosition(): Int
    fun pausePlayer()
    fun releasePLayer()
    fun controlPlayerState(onStateChangedTo: (s: PlayerState) -> Unit)
    fun destroy()
}