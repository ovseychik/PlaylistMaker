package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String, onStateChanged: (s: PlayerState) -> Unit) {
        playerRepository.preparePlayer(url, onStateChanged)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        playerRepository.releasePLayer()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun controlPlayerState(onStateChanged: (s: PlayerState) -> Unit) {
        playerRepository.controlPlayerState(onStateChanged)
    }

}