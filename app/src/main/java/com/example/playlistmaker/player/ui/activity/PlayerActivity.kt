package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.PlaylistsScreenState
import com.example.playlistmaker.library.ui.fragment.NewPlaylistFragment
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.AddTrackState
import com.example.playlistmaker.player.ui.BottomSheetPlaylistsAdapter
import com.example.playlistmaker.player.ui.PlayerToastState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var bundledTrack: Track? = null

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlists = ArrayList<Playlist>()

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val playlistsAdapter = BottomSheetPlaylistsAdapter(playlists) {
        onPlaylistClickDebounce(it)
    }

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

        bundledTrack?.let { track ->
            viewModel.preparePlayer(track.previewUrl ?: return)
            initViews(track)

            lifecycleScope.launch {
                val isFavorite = viewModel.isTackFavorite(track.trackId)
                changeFavoriteIcon(isFavorite)
                track.isFavorite = isFavorite
            }

            bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

            bottomSheetBehavior?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                        }

                        else -> binding.overlay.isVisible = true
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            binding.rvTracks.adapter = playlistsAdapter

            onPlaylistClickDebounce = debounce<Playlist>(
                CLICK_DEBOUNCE_DELAY_MILLIS,
                lifecycleScope,
                false
            ) { playlist ->
                viewModel.onPlaylistClicked(playlist, track)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            viewModel.isFavoriteLiveData.observe(this) { isFavorite ->
                changeFavoriteIcon(isFavorite)
                track.isFavorite = isFavorite
            }

            viewModel.observeState().observe(this) {
                render(it)
            }

            viewModel.observeAddTrackState().observe(this) {
                renderAddTrack(it)
            }

            viewModel.observeToastState().observe(this) { toastState ->
                if (toastState is PlayerToastState.ShowMessage) {
                    showMessage(toastState.message)
                    viewModel.toastWasShown()
                }
            }
            binding.likeButton.setOnClickListener {
                viewModel.onFavoriteClicked(track)
            }

            binding.btnPlay.setOnClickListener { viewModel.controlPlayerState() }

            binding.btnBackPlayer.setOnClickListener {
                finish()
            }

            binding.addToPlaylist.setOnClickListener {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                viewModel.fillData()
            }

            binding.btnNewPlaylist.setOnClickListener {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.player_fragment_container,
                    NewPlaylistFragment.newInstance(true)
                )
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                binding.playerScrollView.isVisible = false
                binding.playerFragmentContainer.isVisible = true
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

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Content -> showContent(state.playlists)
            is PlaylistsScreenState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.rvTracks.isVisible = false
        binding.placeholderTextNoPlaylist.isVisible = true
        binding.placeholderImageNoPlaylist.isVisible = true
        binding.placeholderTextNoPlaylist.setText(R.string.placeholder_no_playlists_yet)
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.rvTracks.isVisible = true
        binding.placeholderTextNoPlaylist.isVisible = false
        binding.placeholderImageNoPlaylist.isVisible = false

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun renderAddTrack(state: AddTrackState) {
        when (state) {
            is AddTrackState.Exist -> {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                viewModel.showToast("${getString(R.string.already_in_playlist)} ${state.playlistTitle}")
            }

            is AddTrackState.Added -> {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                //binding.addToPlaylist.setImageResource(R.drawable.ic_btn_add_to_playlist_on_success)
                viewModel.showToast("${getString(R.string.added_to_playlist)} ${state.playlistTitle}")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

    private fun releaseYear(string: String): String = string.removeRange(4 until string.length)

    companion object {
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}