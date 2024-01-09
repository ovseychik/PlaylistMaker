package com.example.playlistmaker.player.ui

sealed interface PlayerToastState {
    object None : PlayerToastState
    data class ShowMessage(val message: String) : PlayerToastState
}
