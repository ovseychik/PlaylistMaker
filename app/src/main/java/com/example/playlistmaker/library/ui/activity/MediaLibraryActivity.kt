package com.example.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.example.playlistmaker.library.ui.FavoritePlaylistsFragment
import com.example.playlistmaker.library.ui.FavoriteTracksFragment
import com.example.playlistmaker.library.ui.ViewPageMediaLibraryAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {
    private val fragmentList = listOf(
        FavoriteTracksFragment.newInstance(),
        FavoritePlaylistsFragment.newInstance()
    )

    private lateinit var fragmentListTitles: List<String>
    private lateinit var binding: ActivityMediaLibraryBinding

    private lateinit var tabMediator: TabLayoutMediator


    override fun onCreate(savedInstanceState: Bundle?) {

        fragmentListTitles = listOf(
            getString(R.string.tab_favorite_track),
            getString(R.string.tab_playlists)
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPageMediaLibraryAdapter(this, fragmentList)
        binding.viewPagerLibrary.adapter = adapter

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPagerLibrary) { tab, position ->
            tab.text = fragmentListTitles[position]
        }
        tabMediator.attach()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}