package com.example.playlistmaker.library.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


open class NewPlaylistFragment : Fragment() {
    open val viewModel by viewModel<NewPlaylistViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    open val binding get() = _binding!!

    private val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()

    private val requester = PermissionRequester.instance()
    private var imagePath: String? = null

    private var confirmDialog: MaterialAlertDialogBuilder? = null

    var playlistTitleTemp: String = ""
    var playlistDescriptionTemp: String? = null
    var playlistCoverTemp: String? = null
    var playlistTrackIdsTemp: List<String>? = null
    var playlistNumberOfTracksTemp: Int? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri.toString())
                    .placeholder(R.drawable.placeholder_add_photo)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            requireContext().resources.getDimensionPixelSize(R.dimen.album_cover_round_player)
                        )
                    )
                    .into(binding.imageviewPlaylistCover)

                playlistCoverTemp = uri.toString()
            }
        }

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
        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.complete_playlist_creation)
            .setMessage(R.string.data_loss_warning)
            .setNegativeButton(R.string.cancel) { _, _ ->
            }.setPositiveButton(R.string.complete) { _, _ ->
                navigateBack()
            }

        binding.edittextPlaylistTitle.addTextChangedListener(
            getTextWatcherForPlaylistTitle()
        )

        binding.edittextPlaylistDescription.addTextChangedListener(
            getTextWatcherForPlaylistDescription()
        )

        binding.btnBackNewPlaylist.setOnClickListener {
            checkDataForDialog()
        }

        binding.imageviewPlaylistCover.setOnClickListener {
            askPermissions()
        }

        binding.textviewCreatePlaylist.setOnClickListener {
            createPlaylist()
        }

        onBackPress(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("imagePath", imagePath)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("imagePath") != "null") {
                Glide.with(this)
                    .load(savedInstanceState.getString("imagePath"))
                    .placeholder(R.drawable.placeholder_add_photo)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            requireContext().resources.getDimensionPixelSize(R.dimen.album_cover_round_player)
                        )
                    )
                    .into(binding.imageviewPlaylistCover)

                playlistCoverTemp = savedInstanceState.getString("imagePath")!!
            }
        }
    }

    private fun getCheckedStorageConst(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun getTextWatcherForPlaylistTitle(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistTitleTemp = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                binding.textviewCreatePlaylist.isEnabled = s?.isNotEmpty() == true
            }
        }
    }

    private fun getTextWatcherForPlaylistDescription(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistDescriptionTemp = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    private fun setupTextChangeListener() {
        binding.edittextPlaylistTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.textviewCreatePlaylist.isEnabled = s?.isNotEmpty() == true
            }
        })
    }

    private fun askPermissions() {
        lifecycleScope.launch {
            requester.request(getCheckedStorageConst()).collect { result ->
                when (result) {
                    is Granted -> {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    is Denied.NeedsRationale -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.permission_rationale,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Denied.DeniedPermanently -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data =
                            Uri.fromParts("package", requireContext().packageName, null)
                        requireContext().startActivity(intent)
                    }

                    is Cancelled -> {
                        return@collect
                    }
                }
            }
        }
    }

    private fun createPlaylist() {
        val playlist = Playlist(
            id = 0,
            playlistTitle = playlistTitleTemp,
            playlistDescription = playlistDescriptionTemp,
            playlistCoverPath = playlistCoverTemp,
            trackIds = playlistTrackIdsTemp,
            numberOfTracks = playlistNumberOfTracksTemp
        )

        newPlaylistViewModel.createPlaylist(playlist)

        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created, playlist.playlistTitle),
            Toast.LENGTH_SHORT
        ).show()

        navigateBack()
    }

    private fun checkDataForDialog() {
        if (playlistCoverTemp != null ||
            playlistTitleTemp.isNotEmpty() ||
            playlistDescriptionTemp?.isNotEmpty() == true
        ) {
            confirmDialog?.show()
        } else {
            navigateBack()
        }
    }

    open fun onBackPress(switch: Boolean) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(switch) {
                override fun handleOnBackPressed() {
                    checkDataForDialog()
                }
            })
    }

    private fun navigateBack() {
        if (parentPlayerActivity) {
            requireActivity().findViewById<ScrollView>(R.id.player_scroll_view).isVisible = true
            parentFragmentManager.popBackStack()
            parentPlayerActivity = false
        } else {
            findNavController().popBackStack()
        }
    }

    companion object {
        private var parentPlayerActivity = false
        fun newInstance(flagParentPlayerActivity: Boolean): NewPlaylistFragment {
            parentPlayerActivity = flagParentPlayerActivity
            return NewPlaylistFragment()
        }
    }
}