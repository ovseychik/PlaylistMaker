package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritePlaylistsBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.PlaylistAdapter
import com.example.playlistmaker.library.ui.PlaylistsScreenState
import com.example.playlistmaker.library.ui.viewmodel.PlaylistsFragmentViewModel
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val playlistViewModel by viewModel<PlaylistsFragmentViewModel>()
    private val mainViewModel by activityViewModel<MainViewModel>()


    private var _binding: FragmentFavoritePlaylistsBinding? = null
    private val binding get() = _binding!!

    private var playlists = ArrayList<Playlist>()
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val playlistAdapter = PlaylistAdapter(playlists) { onPlaylistClickDebounce(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritePlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewPlaylists.adapter = playlistAdapter

        playlistViewModel.fillData()

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            lifecycleScope,
            false
        ) { playlist ->
            mainViewModel.setPlaylist(playlist)
            findNavController().navigate(
                R.id.playlistDetailsFragment,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.fillData()
    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Content -> showContent(state.playlists)
            is PlaylistsScreenState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.recyclerviewPlaylists.isVisible = false
        binding.buttonCreateNewPlaylist.isVisible = true
        binding.textPlaceholder.isVisible = true
        binding.imageNoFavoriteTracks.isVisible = true
        binding.textPlaceholder.setText(R.string.placeholder_no_playlists_yet)
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerviewPlaylists.isVisible = true
        binding.buttonCreateNewPlaylist.isVisible = true
        binding.textPlaceholder.isVisible = false
        binding.imageNoFavoriteTracks.isVisible = false
        binding.textPlaceholder.isVisible = false

        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}