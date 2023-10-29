package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritePlaylistsBinding
import com.example.playlistmaker.library.ui.viewmodel.FavoritePlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoritePlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = FavoritePlaylistsFragment()
    }

    private val favoritePlaylistsFragmentViewModel: FavoritePlaylistsFragmentViewModel by activityViewModel()


    private var _binding: FragmentFavoritePlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritePlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}