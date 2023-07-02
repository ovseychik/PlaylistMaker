package com.example.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.PlayerState
import com.example.playlistmaker.domain.api.player.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
        const val REFRESH_TRACK_PROGRESS = 100L
    }

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
    private lateinit var buttonPlay: FloatingActionButton

    private val playerInteractor: PlayerInteractor = Creator.providePlayerInteractor()

    private var playerState = PlayerState.STATE_DEFAULT
    private var mainThreadHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>(TRACK_FOR_PLAYER)

        mainThreadHandler = Handler(Looper.getMainLooper())

        initViews()
        if (track != null) {
            fillViewWith(track)
            buttonPlay = findViewById(R.id.btnPlay)
            playerInteractor.preparePlayer(track.previewUrl) { state ->
                when (state) {
                    PlayerState.STATE_PREPARED -> {
                        buttonPlay.isEnabled = true
                        buttonPlay.setImageResource(R.drawable.ic_button_play)
                    }

                    else -> {}
                }
            }

            buttonPlay.setOnClickListener {
                playerInteractor.controlPlayerState { state ->
                    when (state) {
                        PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> {
                            buttonPlay.setImageResource(R.drawable.ic_button_play)
                            mainThreadHandler?.removeCallbacks(runThread)
                        }

                        PlayerState.STATE_PLAYING -> {
                            buttonPlay.setImageResource(R.drawable.ic_pause)
                            currentTimeControl()
                        }

                        else -> {}
                    }
                }
            }
        } else {
            Snackbar.make(View(this), R.string.snackbar_error_message, LENGTH_LONG)
                .show()
        }

        buttonBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        mainThreadHandler?.removeCallbacks(runThread)
    }

/*    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
    }*/

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
        buttonBack = findViewById(R.id.btn_back_player)
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
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_round_player)))
            .placeholder(R.drawable.ic_album_cover_placeholder_hires)
            .into(songArtwork)
    }

    private fun releaseYear(string: String): String = string.removeRange(4 until string.length)

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

    private val runThread = object : Runnable {
        override fun run() {
            currentTime.text =
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(playerInteractor.getCurrentPosition())

            mainThreadHandler.postDelayed(
                this,
                REFRESH_TRACK_PROGRESS
            )
        }
    }

    private fun currentTimeControl() {
        when (this.playerState) {
            PlayerState.STATE_PLAYING -> {
                mainThreadHandler.postDelayed(
                    runThread,
                    REFRESH_TRACK_PROGRESS
                )
            }

            else -> {}
        }
    }
}