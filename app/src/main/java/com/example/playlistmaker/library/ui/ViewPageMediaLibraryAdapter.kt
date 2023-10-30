package com.example.playlistmaker.library.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.library.ui.activity.MediaLibraryActivity

class ViewPageMediaLibraryAdapter(
    mediaLibraryActivity: MediaLibraryActivity,
    private val list: List<Fragment>
) : FragmentStateAdapter(mediaLibraryActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}