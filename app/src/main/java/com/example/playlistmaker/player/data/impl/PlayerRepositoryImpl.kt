package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerState = PlayerState.STATE_DEFAULT

    override fun preparePlayer(url: String, onStateChangedTo: (s: PlayerState) -> Unit) {
        if (url.isNotEmpty()) {
            mediaPlayer.apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    playerState = PlayerState.STATE_PREPARED
                    onStateChangedTo(PlayerState.STATE_PREPARED)
                }
                setOnCompletionListener {
                    playerState = PlayerState.STATE_PREPARED
                    onStateChangedTo(PlayerState.STATE_PREPARED)
                }
            }
        } else {
            Unit // do nothing
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
        this.mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun releasePLayer() {
        this.mediaPlayer.release()
    }

    override fun controlPlayerState(onStateChangedTo: (s: PlayerState) -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
                playerState = PlayerState.STATE_PAUSED
                onStateChangedTo(PlayerState.STATE_PAUSED)
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
                playerState = PlayerState.STATE_PLAYING
                onStateChangedTo(PlayerState.STATE_PLAYING)
            }

            else -> {}
        }
    }

}