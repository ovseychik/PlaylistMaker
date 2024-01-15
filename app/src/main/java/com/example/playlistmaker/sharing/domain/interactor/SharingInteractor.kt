package com.example.playlistmaker.sharing.domain.interactor

interface SharingInteractor {
    fun shareApp(url: String)
    fun openLegal(urlLegal: String)
    fun contactSupport(email: String, subject: String, body: String)
    fun sharePlaylist(message: String)
}