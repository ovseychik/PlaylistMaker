package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.viewmodel.PlaylistEditingViewModel
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditingFragment : NewPlaylistFragment() {
    override val viewModel by viewModel<PlaylistEditingViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    override val binding get() = _binding!!

    private var playlist: Playlist? = null
    private val mainViewModel by activityViewModel<MainViewModel>()
    private var playlistIdTemp: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = mainViewModel.getPlaylist().value!!

        playlistIdTemp = playlist?.id!!
        playlistTitleTemp = playlist?.playlistTitle!!
        playlistDescriptionTemp = playlist?.playlistDescription
        playlistTrackIdsTemp = playlist?.trackIds
        playlistNumberOfTracksTemp = playlist?.numberOfTracks

        if (playlist?.playlistCoverPath != null) {
            playlistCoverTemp = playlist?.playlistCoverPath!!
        }

        setViewAttributes()

        initPlaylistData(playlist!!)

        binding.textviewCreatePlaylist.setOnClickListener {
            updatePlaylist()
        }

        binding.btnBackNewPlaylist.setOnClickListener {
            findNavController().popBackStack()
        }

        onBackPress(false)
    }

    override fun onBackPress(switch: Boolean) {
    }

    private fun setViewAttributes() {
        binding.textviewNewPlaylist.text = requireActivity().resources.getString(R.string.edit)
        binding.textviewCreatePlaylist.text = requireActivity().resources.getString(R.string.save)
    }

    private fun initPlaylistData(playlist: Playlist) {
        Glide.with(this)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.placeholder_add_photo)
            .transform(
                CenterCrop(),
                RoundedCorners(
                    requireContext().resources.getDimensionPixelSize(R.dimen.album_cover_round_player)
                )
            )
            .into(binding.imageviewPlaylistCover)

        binding.edittextPlaylistTitle.setText(playlist.playlistTitle)

        binding.edittextPlaylistDescription.setText(playlist.playlistDescription)
    }

    private fun updatePlaylist() {
        val playlist = Playlist(
            playlistIdTemp,
            playlistTitleTemp,
            playlistDescriptionTemp,
            playlistCoverTemp,
            playlistTrackIdsTemp,
            playlistNumberOfTracksTemp
        )

        viewModel.updatePlaylist(playlist)
        mainViewModel.setPlaylist(playlist)
        findNavController().popBackStack()
    }

}