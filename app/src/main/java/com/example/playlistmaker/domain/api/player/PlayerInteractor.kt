package com.example.playlistmaker.domain.api.player

import com.example.playlistmaker.domain.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String, onStateChanged: (s: PlayerState) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun controlPlayerState(onStateChanged: (s: PlayerState) -> Unit)
}