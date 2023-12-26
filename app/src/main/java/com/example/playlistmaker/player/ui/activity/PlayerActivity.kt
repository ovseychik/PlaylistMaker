package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var bundledTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.statePlayerLiveData().observe(this) { state ->
            changeState(state)
        }

        bundledTrack =
            savedInstanceState?.getParcelable(TRACK_FOR_PLAYER)
                ?: intent.getParcelableExtra(TRACK_FOR_PLAYER)

        // Такая обёртка, чтобы не расставлять везде безопасный или принудительный вызов
        // (track? track!!)
        bundledTrack?.let { track ->
            viewModel.preparePlayer(track.previewUrl ?: return)
            initViews(track)

            viewModel.isFavoriteLiveData.observe(this) { isFavorite ->
                changeFavoriteIcon(isFavorite)
                track.isFavorite = isFavorite
            }

            lifecycleScope.launch {
                val isFavorite = viewModel.isTackFavorite(track.trackId)
                changeFavoriteIcon(isFavorite)
                track.isFavorite = isFavorite
            }

            binding.likeButton.setOnClickListener {
                viewModel.onFavoriteClicked(track)
            }

            binding.btnPlay.setOnClickListener { viewModel.controlPlayerState() }

            binding.btnBackPlayer.setOnClickListener {
                finish()
            }
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
            year.text = releaseYear(track.releaseDate ?: R.string.no_info.toString())
            genre.text = track.primaryGenreName
            albumName.text = track.collectionName
        }
    }

    private fun changeFavoriteIcon(isFavorite: Boolean) {
        val buttonImageResource = if (isFavorite) {
            R.drawable.ic_button_like_pressed
        } else {
            R.drawable.ic_button_like
        }
        binding.likeButton.setImageResource(buttonImageResource)
    }

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

    private fun releaseYear(string: String): String = string.removeRange(4 until string.length)

    companion object {
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
    }
}