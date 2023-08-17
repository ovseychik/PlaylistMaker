package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
    }

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var bundledProgressTime: String = ""
    private var bundledTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.statePlayerLiveData().observe(this) { state ->
            changeState(state)
        }


        val track = intent.getParcelableExtra<Track>(TRACK_FOR_PLAYER)
        bundledTrack = track

        val url = if (track != null) track.previewUrl else bundledTrack?.previewUrl

        viewModel.preparePlayer(url!!)

        binding.btnPlay.setOnClickListener { viewModel.controlPlayerState() }

        initViews(bundledTrack!!)

        binding.btnBackPlayer.setOnClickListener {
            viewModel.onDestroy()
            super.finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK_FOR_PLAYER, bundledTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bundledTrack = savedInstanceState.getParcelable(TRACK_FOR_PLAYER)
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    override fun onResume() {
        viewModel.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

    private fun changeState(state: PlayerState) {
        if (state == PlayerState.STATE_PAUSED) {
            binding.btnPlay.setImageResource(R.drawable.ic_button_play)
        }
        if (state == PlayerState.STATE_PLAYING) {
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
            binding.currentTime.text = viewModel.getCurrentPosition()
        }
        if (state == PlayerState.STATE_PREPARED) {
            binding.btnPlay.setImageResource(R.drawable.ic_button_play)
        }
        if (state == PlayerState.STATE_DEFAULT) {
            binding.btnPlay.setImageResource(R.drawable.ic_button_play)
            binding.currentTime.text = "00:00"
        }
    }

    private fun initViews(track: Track) {
        with(binding) {
            currentTime.text = millisFormat(track)
            trackName.text = track.trackName
            artistName.text = track.artistName
            time.text = millisFormat(track)
            Glide.with(songArtwork)
                .load(viewModel.getCoverArtWork(track.artworkUrl100))
                .placeholder(R.drawable.ic_album_cover_placeholder_hires)
                .into(songArtwork)
            country.text = track.country
            year.text = releaseYear(track.releaseDate)
            genre.text = track.primaryGenreName
            albumName.text = track.collectionName
        }
    }

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

    private fun releaseYear(string: String): String = string.removeRange(4 until string.length)

}