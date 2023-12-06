package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerRepositoryImpl() : PlayerRepository {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT

    override fun preparePlayer(url: String, onStateChangedTo: (s: PlayerState) -> Unit) {
        if (playerState == PlayerState.STATE_DEFAULT) {
            with(mediaPlayer) {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    playerState = PlayerState.STATE_PREPARED
                    onStateChangedTo(PlayerState.STATE_PREPARED)
                }
                setOnCompletionListener {
                    playerState = PlayerState.STATE_DEFAULT
                    onStateChangedTo(PlayerState.STATE_DEFAULT)
                }
            }
        }
    }

    override fun startPlayer() {
        this.mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun getCurrentPosition(): Int {
        return this.mediaPlayer.currentPosition
    }

    override fun pausePlayer() {
        try {
            this.mediaPlayer.pause()
            playerState = PlayerState.STATE_PAUSED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            releasePLayer()
        }
    }

    override fun releasePLayer() {
        this.mediaPlayer.release()
        playerState = PlayerState.STATE_DEFAULT
    }

    override fun controlPlayerState(onStateChangedTo: (s: PlayerState) -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
                onStateChangedTo(PlayerState.STATE_PAUSED)
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED, PlayerState.STATE_DEFAULT -> {
                startPlayer()
                onStateChangedTo(PlayerState.STATE_PLAYING)
            }

            else -> {}
        }
    }

}