package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
//import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageView>(R.id.btn_back)
        buttonBack.setOnClickListener() {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }

}