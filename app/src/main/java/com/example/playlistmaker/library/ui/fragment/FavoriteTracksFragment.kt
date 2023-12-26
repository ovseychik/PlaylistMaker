package com.example.playlistmaker.library.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.ui.FavoritesScreenState
import com.example.playlistmaker.library.ui.viewmodel.FavoriteTracksFragmentViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoriteTracksFragment : Fragment() {

    private val favoriteTracksFragmentViewModel: FavoriteTracksFragmentViewModel by activityViewModel()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackAdapter = TrackAdapter(ArrayList()) {
        onTrackClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerFavoriteTracklist.adapter = trackAdapter

        favoriteTracksFragmentViewModel.fillData()

        favoriteTracksFragmentViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(PlayerActivity.TRACK_FOR_PLAYER, track)
            startActivity(playerIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        favoriteTracksFragmentViewModel.fillData()
    }

    private fun render(state: FavoritesScreenState) {
        when (state) {
            is FavoritesScreenState.Content -> showContent(state.tracks)
            is FavoritesScreenState.Empty -> showEmpty(state.emptyMessageRes.toString())
        }
    }

    private fun showEmpty(emptyMessage: String) {
        binding.recyclerFavoriteTracklist.isVisible = false
        binding.textPlaceholder.isVisible = true
        binding.imageNoFavoriteTracks.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerFavoriteTracklist.isVisible = true
        binding.textPlaceholder.isVisible = false
        binding.imageNoFavoriteTracks.isVisible = false

        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}