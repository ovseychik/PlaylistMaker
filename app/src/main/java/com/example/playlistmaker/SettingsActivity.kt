package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

const val SETTINGS_PREFERENCES = "settings_preferences"
const val SWITCH_DARK_THEME = "switch_dark_theme"

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val buttonBack = findViewById<ImageView>(R.id.btn_back)
        val buttonShare = findViewById<TextView>(R.id.btn_share)
        val buttonSupport = findViewById<TextView>(R.id.btn_support)
        val buttonLegal = findViewById<TextView>(R.id.btn_legal)
        val sharedPrefsSettings = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)

        //Переключатель темы
        themeSwitcher.isChecked = sharedPrefsSettings.getBoolean(SWITCH_DARK_THEME, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefsSettings.edit()
                .putBoolean(SWITCH_DARK_THEME, checked)
                .apply()
        }

        //Кнопка поделиться
        buttonShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            intent.type = "text/plain"
            startActivity(intent)
        }

        //Кнопка связаться с поддержкой
        buttonSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(intent)
        }

        //Ссылка на пользовательское соглашение
        buttonLegal.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.legal_link)))
            startActivity(intent)
        }

        //Кнопка назад
        buttonBack.setOnClickListener {
            finish()
        }
    }

}