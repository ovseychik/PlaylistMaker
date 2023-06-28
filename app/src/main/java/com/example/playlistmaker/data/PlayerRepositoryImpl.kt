package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.PlayerState
import com.example.playlistmaker.domain.api.player.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private var playerState = PlayerState.STATE_DEFAULT
    private lateinit var mediaPlayer: MediaPlayer

    override fun preparePlayer(url: String, onStateChangedTo: (s: PlayerState) -> Unit) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
            onStateChangedTo(PlayerState.STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            onStateChangedTo(PlayerState.STATE_PREPARED)
        }
        this.mediaPlayer = mediaPlayer
    }

    override fun startPlayer() {
        this.mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun pausePlayer() {
        this.mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun releasePLayer() {
        this.mediaPlayer.release()
    }

    override fun controlPlayerState(onStateChangedTo: (s: PlayerState) -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> pausePlayer()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            else -> {}
        }
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

}