package com.example.playlistmaker

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
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

    private var mainThreadHandler: Handler? = null

    private val runThread = object : Runnable {
        override fun run() {
            currentTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

            mainThreadHandler?.postDelayed(
                this,
                REFRESH_TRACK_PROGRESS
            )
        }
    }

    private fun currentTimeControl() {
        when (playerState) {
            STATE_PLAYING -> {
                mainThreadHandler?.postDelayed(
                    runThread,
                    REFRESH_TRACK_PROGRESS
                )
            }

            STATE_PAUSED -> {
                mainThreadHandler?.removeCallbacks(runThread)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>(TRACK_FOR_PLAYER)

        mainThreadHandler = Handler(Looper.getMainLooper())

        initViews()
        if (track != null) {
            fillViewWith(track)
            buttonPlay = findViewById(R.id.btnPlay)
            preparePlayer(track)

            buttonPlay.setOnClickListener {
                playbackControl()
                currentTimeControl()
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
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(runThread)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
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

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            buttonPlay.setImageResource(R.drawable.ic_button_play)
            mainThreadHandler?.removeCallbacks(runThread)
            currentTime.text = "00:00"
            // Если по ТЗ потребуется отображать полную длину трека после окончания
            // currentTime.text = millisFormat(track)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.ic_button_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


}

