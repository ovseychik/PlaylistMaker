package com.example.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmaker.library.ui.fragment.FavoritePlaylistsFragment
import com.example.playlistmaker.library.ui.fragment.FavoriteTracksFragment
import com.example.playlistmaker.library.ui.ViewPageMediaLibraryAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : Fragment() {
    private val fragmentList = listOf(
        FavoriteTracksFragment.newInstance(),
        FavoritePlaylistsFragment.newInstance()
    )

    private lateinit var fragmentListTitles: List<String>
    private lateinit var binding: FragmentMediaLibraryBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerLibrary.adapter = ViewPageMediaLibraryAdapter(
            childFragmentManager,
            lifecycle,
            fragmentList
        )

        tabMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPagerLibrary) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.tab_favorite_track)
                    else -> getString(R.string.tab_playlists)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}