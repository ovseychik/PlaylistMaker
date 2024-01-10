package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailFields

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareApp(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(shareIntent)
    }

    override fun openLegal(urlLegal: String) {
        val legalIntent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse(urlLegal)
        }
        context.startActivity(legalIntent)
    }

    override fun contactSupport(supportEmail: EmailFields) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmail.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmail.body)
        }
        context.startActivity(supportIntent)
    }

    override fun sharePlaylist(message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(shareIntent)
    }

}