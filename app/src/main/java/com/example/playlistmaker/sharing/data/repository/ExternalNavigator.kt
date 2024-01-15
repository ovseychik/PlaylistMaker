package com.example.playlistmaker.sharing.data.repository

import com.example.playlistmaker.sharing.domain.model.EmailFields

interface ExternalNavigator {
    fun shareApp(url: String)
    fun openLegal(urlLegal: String)
    fun contactSupport(supportEmail: EmailFields)
    fun sharePlaylist(message: String)
}