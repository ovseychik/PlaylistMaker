package com.example.playlistmaker.library.ui.viewmodel

import com.example.playlistmaker.library.domain.db.PlaylistsInteractor

class PlaylistEditingViewModel(val playlistsInteractor: PlaylistsInteractor) :
    NewPlaylistViewModel(playlistsInteractor) {
}