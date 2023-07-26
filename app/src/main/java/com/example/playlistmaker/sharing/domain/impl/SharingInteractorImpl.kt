package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.data.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    private fun getShareLink(urlCourse: String): String {
        return urlCourse
    }

    override fun shareApp(url: String) {
        TODO("Not yet implemented")
    }

    override fun openLegal(urlLegal: String) {
        TODO("Not yet implemented")
    }

    override fun contactSupport(email: String, subject: String, body: String) {
        TODO("Not yet implemented")
    }

}