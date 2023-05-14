package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var timeAttributes: TextView
    private lateinit var albumNameAttributes: TextView
    private lateinit var yearAttributes: TextView
    private lateinit var genreAttributes: TextView
    private lateinit var countryAttributes: TextView
    private lateinit var currentTime: TextView
    private lateinit var songArtwork: ImageView
    private lateinit var buttonBack: ImageView
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = gson.fromJson(
            intent.getStringExtra(SearchActivity.TRACKS_FOR_PLAYER),
            Track::class.java
        )

        initViews()
        fillViewWith(track)

        buttonBack.setOnClickListener {
            // Ждем спринт по coroutines, чтобы не убить проигрывание музыки?
            finish()
        }
    }

    private fun initViews() {
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        currentTime = findViewById(R.id.currentTime)
        timeAttributes = findViewById(R.id.time)
        albumNameAttributes = findViewById(R.id.albumName)
        yearAttributes = findViewById(R.id.year)
        genreAttributes = findViewById(R.id.genre)
        countryAttributes = findViewById(R.id.country)
        songArtwork = findViewById(R.id.songArtwork)
        buttonBack = findViewById(R.id.btn_back)
    }

    private fun fillViewWith(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        timeAttributes.text = millisFormat(track)
        albumNameAttributes.text = track.collectionName
        yearAttributes.text = releaseYear(track.releaseDate)
        genreAttributes.text = track.primaryGenreName
        countryAttributes.text = track.country
        currentTime.text = millisFormat(track)
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_round_player)))
            .placeholder(R.drawable.ic_album_cover_placeholder_hires)
            .into(songArtwork)
    }


    private fun releaseYear(string: String): String = string.removeRange(4 until string.length)

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

}