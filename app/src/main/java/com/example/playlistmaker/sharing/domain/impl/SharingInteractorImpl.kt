package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.data.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailFields

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    private fun getShareLink(urlCourse: String): String {
        return urlCourse
    }

    override fun shareApp(url: String) {
        externalNavigator.shareApp(getShareLink(url))
    }

    private fun getLegalLink(urlLegal: String): String {
        return urlLegal
    }

    override fun openLegal(urlLegal: String) {
        externalNavigator.openLegal(getLegalLink(urlLegal))
    }

    override fun contactSupport(email: String, subject: String, body: String) {
        externalNavigator.contactSupport(
            EmailFields(
                email,
                subject,
                body
            )
        )
    }

    override fun sharePlaylist(message: String) {
        externalNavigator.sharePlaylist(message)
    }

}