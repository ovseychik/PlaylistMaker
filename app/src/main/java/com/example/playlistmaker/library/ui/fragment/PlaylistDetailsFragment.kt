package com.example.playlistmaker.library.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.PlaylistDetailsScreenState
import com.example.playlistmaker.library.ui.TrackInPlayListAdapter
import com.example.playlistmaker.library.ui.viewmodel.PlaylistDetailsViewModel
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {
    private val playlistDetailsViewModel: PlaylistDetailsViewModel by viewModel()
    private val mainViewModel by activityViewModel<MainViewModel>()

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlist: Playlist? = null
    private val tracks = mutableListOf<Track>()
    private var listIdTracksTemp: ArrayList<String>? = null
    private var trackInPlaylistAdapter: TrackInPlayListAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var playlistTemp: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = mainViewModel.getPlaylist().value!!
        Log.d("PLAYLIST oncreate", playlist.toString())
        playlistDetailsViewModel.getFlowPlaylistById(playlist!!.id)

        playlistDetailsViewModel.getStatePlaylistLiveData().observe(viewLifecycleOwner) {
            renderTracksInPlaylist(it)
        }

        binding.imageviewBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(PlayerActivity.TRACK_FOR_PLAYER, track)
            requireContext().startActivity(playerIntent)
        }

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        binding.tracksBottomSheet.post {
            val buttonLocation = IntArray(2)
            binding.imageviewShareButton.getLocationOnScreen(buttonLocation)
            val openMenuHeightFromBottom =
                binding.root.height - buttonLocation[1] - resources.getDimensionPixelSize(R.dimen.margin8)
            tracksBottomSheetBehavior?.peekHeight = openMenuHeightFromBottom.coerceAtLeast(100)
        }

        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.playlistOverlay.isVisible = false
                        binding.recyclerviewTrack.isVisible = true
                    }

                    else -> {
                        binding.playlistOverlay.isVisible = true
                        binding.recyclerviewTrack.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.imageviewMenuButton.setOnClickListener {
            playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            showMenu(playlist!!)
        }

        binding.imageviewShareButton.setOnClickListener {
            Log.d("PLAYLIST onclick", playlist.toString())
            share()
        }
    }

    private fun renderTracksInPlaylist(state: PlaylistDetailsScreenState) {
        when (state) {
            is PlaylistDetailsScreenState.NoTracks -> showStateNoTracks()

            is PlaylistDetailsScreenState.WithTracks -> showStateWithTracks(
                state.listTracks,
                state.durationSumTime
            )

            is PlaylistDetailsScreenState.DeletedTrack -> showStateDeletedTrack(
                state.listTracks,
                state.durationSumTime,
                state.counterTracks,
                state.playlist
            )

            is PlaylistDetailsScreenState.DeletedPlaylist -> {
                showStateDeletedPlaylist()
            }

            is PlaylistDetailsScreenState.InitPlaylist -> initPlaylist(state.playlist)

            is PlaylistDetailsScreenState.Error -> Log.e(
                "ErrorQueryOnDb",
                getString(R.string.empty_playlist)
            )
        }
    }

    private fun showStateNoTracks() {
        binding.textviewEmptyPlaylistMessage.isVisible = true
        binding.textviewEmptyPlaylistMessage.setText(R.string.empty_playlist)
        binding.textviewTracksTime.text = requireActivity().resources.getQuantityString(
            R.plurals.minutes,
            0, 0
        )
        binding.textviewNumberOfTracks.text = requireActivity().resources.getQuantityString(
            R.plurals.track_count,
            0, 0
        )
        tracks.clear()
        trackInPlaylistAdapter?.notifyDataSetChanged()
    }

    private fun showStateWithTracks(listTracks: List<Track>, durationSumTime: Long) {
        binding.textviewEmptyPlaylistMessage.isVisible = false
        val updatedTracks = listTracks.map { track ->
            track.copy(artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "60x60bb.jpg"))
        }
        binding.textviewTracksTime.text = requireActivity().resources.getQuantityString(
            R.plurals.minutes,
            durationSumTime.toInt(), durationSumTime
        )
        tracks.clear()
        tracks.addAll(updatedTracks.reversed())
        trackInPlaylistAdapter?.notifyDataSetChanged()
        binding.recyclerviewTrack.isVisible = true
        if (updatedTracks.isEmpty()) showMistakeDialog()
    }

    private fun showStateDeletedTrack(
        listTracks: List<Track>,
        durationSumTime: Long,
        counterTracks: Int,
        playlist: Playlist
    ) {
        Log.d("PLAYLIST SSDT playlist ", playlist.toString())
        Log.d("PLAYLIST SSDT counter", counterTracks.toString())
        val updatedTracks = listTracks.map { track ->
            track.copy(artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "60x60bb.jpg"))
        }
        binding.textviewTracksTime.text = requireActivity().resources.getQuantityString(
            R.plurals.minutes,
            durationSumTime.toInt(), durationSumTime
        )
        binding.textviewNumberOfTracks.text = requireActivity().resources.getQuantityString(
            R.plurals.track_count,
            counterTracks, counterTracks
        )
        tracks.clear()
        tracks.addAll(updatedTracks)
        trackInPlaylistAdapter?.notifyDataSetChanged()
    }

    private fun showStateDeletedPlaylist() {
        findNavController().popBackStack()
    }

    private fun initPlaylist(playlist: Playlist?) {

        playlistTemp = playlist

        listIdTracksTemp = if (playlist?.trackIds?.isNotEmpty() == true) {
            playlist.trackIds as ArrayList<String>?
        } else {
            ArrayList()
        }

        playlistDetailsViewModel.getTracksFromPlaylistByIds(listIdTracksTemp)

        binding.apply {
            Glide.with(requireActivity())
                .load(playlist?.playlistCoverPath)
                .placeholder(R.drawable.ic_album_cover_placeholder_hires)
                .into(imageviewPlaylistCover)

            textviewPlaylistTitle.text = playlist?.playlistTitle
            if (playlist?.playlistDescription.isNullOrEmpty()) {
                textviewDescription.isVisible = false
            } else {
                textviewDescription.text = playlist?.playlistDescription
            }

            textviewTracksTime.text = requireActivity().resources.getQuantityString(
                R.plurals.minutes,
                0, 0
            )
            textviewNumberOfTracks.text = playlist?.numberOfTracks?.let {
                requireActivity().resources.getQuantityString(
                    R.plurals.track_count,
                    it, playlist.numberOfTracks
                )
            }

            trackInPlaylistAdapter = TrackInPlayListAdapter(
                tracks,
                MaterialAlertDialogBuilder(
                    requireActivity(),
                    R.style.AlertDialogTheme
                )
                    .setTitle(R.string.delete_track)
                    .setMessage(R.string.delete_track_warning),
                { track ->
                    onTrackClickDebounce(track)
                },
                { track ->
                    playlist?.id?.let {
                        playlistDetailsViewModel.removeTrackFromPlaylist(
                            it,
                            track.trackId
                        )
                    }
                })

            recyclerviewTrack.adapter = trackInPlaylistAdapter
        }
    }

    private fun showMistakeDialog() {
        MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme)
            .setMessage(R.string.share_empty_playlist)
            .setPositiveButton(
                R.string.ok
            ) { _, _ -> }.show()

    }

    private fun showMenu(playlist: Playlist) {
        binding.apply {
            Glide.with(requireActivity())
                .load(playlist.playlistCoverPath)
                .placeholder(R.drawable.ic_album_cover_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        requireContext().resources.getDimensionPixelSize(R.dimen.album_cover_round_list)
                    )
                )
                .into(playlistItem.imageviewPlaylistCoverSmall)

            playlistItem.textviewPlaylistTitle.text = playlist.playlistTitle
            val countText = playlist.numberOfTracks?.let {
                requireActivity().resources.getQuantityString(
                    R.plurals.track_count,
                    it, playlist.numberOfTracks
                )
            }
            playlistItem.textviewNumberOfTracks.text = countText

            textviewDeleteTextMenu.setOnClickListener {
                playlistBottomSheetBehavior?.apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }
                MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme)
                    .setTitle(R.string.delete_playlist)
                    .setMessage(
                        requireActivity().resources.getString(R.string.delete_playlist_warning)
                            .format(playlist.playlistTitle)
                    )
                    .setNegativeButton(R.string.cancel) { _, _ ->
                    }
                    .setPositiveButton(R.string.delete) { _, _ ->
                        playlistDetailsViewModel.deletePlaylist(playlist)
                    }.show()
            }

            textviewShareTextMenu.setOnClickListener {
                playlistBottomSheetBehavior?.apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }

                share()
            }

            textviewUpdateTextMenu.setOnClickListener {
                mainViewModel.setPlaylist(playlist)
                findNavController().navigate(
                    R.id.playlistEditingFragment
                )
            }
        }
    }

    private fun share() {
        Log.d("PLAYLIST SHARE", playlist.toString())
        if (playlist?.numberOfTracks!! > 0) trackInPlaylistAdapter?.let {
            playlistDetailsViewModel.sharePlaylist(
                playlist!!,
                it.tracks
            )
        }
        else showMistakeDialog()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}