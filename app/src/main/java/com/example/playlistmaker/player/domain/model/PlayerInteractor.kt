package com.example.playlistmaker.player.domain.model

import android.media.MediaPlayer

interface PlayerInteractor {
    fun preparePlayer(url: String, onStateChanged: (s: PlayerState) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun controlPlayerState(onStateChanged: (s: PlayerState) -> Unit)
    fun getPlayer(): MediaPlayer
}