package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel by viewModel<SearchViewModel>()

    private var textWatcher: TextWatcher? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var searchText: String = ""
    //private val trackList = ArrayList<Track>()

    private val trackAdapter = TrackAdapter(ArrayList()) {
        searchViewModel.putTrackToHistory(it)
        onTrackClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val playerIntent = Intent(requireActivity(), PlayerActivity::class.java)
            playerIntent.putExtra(PlayerActivity.TRACK_FOR_PLAYER, track)
            startActivity(playerIntent)
        }

        binding.recyclerTrackList.adapter = trackAdapter

        searchViewModel.stateObserver().observe(viewLifecycleOwner) {
            render(it)
        }

        searchViewModel.trackList.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateTracks(tracks)
        }

        searchViewModel.fillHistory()

        binding.inputSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus &&
                binding.inputSearch.text.isEmpty() &&
                searchViewModel.trackList.value?.isNotEmpty() == true
            ) {
                showHistoryScreen()
            } else {
                hideHistoryScreen()
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = binding.inputSearch.text.toString()
                binding.clearTextSearch.visibility = clearButtonVisibility(s)
                searchViewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                if (!s.isNullOrEmpty()) {
                    binding.recyclerTrackList.visibility = View.VISIBLE
                    hideHistoryScreen()
                }

                searchViewModel.fillHistory()

                if (s?.isEmpty() == true && searchViewModel.trackList.value?.isNotEmpty() == true) {
                    showHistoryScreen()
                } else {
                    binding.recyclerTrackList.visibility = View.GONE
                    binding.placeHolder.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { binding.inputSearch.addTextChangedListener(it) }

        binding.clearTextSearch.setOnClickListener {
            binding.inputSearch.setText("")
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)

            if (searchViewModel.trackList.value?.isNotEmpty() == true) {
                showHistoryScreen()
            } else {
                hideHistoryScreen()
            }
        }

        binding.placeholderRefreshButton.setOnClickListener {
            searchViewModel.searchTrack(searchText)
        }

        binding.historyClear.setOnClickListener {
            hideHistoryScreen()
            binding.recyclerTrackList.visibility = View.VISIBLE
            searchViewModel.clearHistory()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.inputSearch.removeTextChangedListener(it) }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int = if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.Success -> {
                showContent(state.data as ArrayList<Track>)
                binding.searchHistoryHeader.visibility = View.GONE
                binding.historyClear.visibility = View.GONE
            }

            is SearchScreenState.Error -> {
                showError(state.message.toString())
                binding.searchPlaceholderText.setText(R.string.check_network)
                binding.searchPlaceHolderImage.setImageResource(R.drawable.no_internet)
                binding.placeholderRefreshButton.visibility = View.VISIBLE
            }

            is SearchScreenState.Empty -> {
                showEmpty(state.message.toString())
                binding.searchPlaceholderText.setText(R.string.nothing_found)
                binding.searchPlaceHolderImage.setImageResource(R.drawable.nothing_found)
                binding.placeholderRefreshButton.visibility = View.GONE
            }

            is SearchScreenState.History -> {
                showContent(state.history as ArrayList<Track>)
                showHistoryScreen()
            }
        }
    }

    private fun showHistoryScreen() {
        binding.placeHolder.visibility = View.GONE
        if (searchViewModel.trackList.value?.isNotEmpty() == true) {
            binding.searchHistoryHeader.visibility = View.VISIBLE
            binding.historyClear.visibility = View.VISIBLE
            binding.recyclerTrackList.visibility = View.VISIBLE
        }
    }

    private fun hideHistoryScreen() {
        binding.searchHistoryHeader.visibility = View.GONE
        binding.historyClear.visibility = View.GONE
    }

    private fun showLoading() {
        binding.recyclerTrackList.visibility = View.GONE
        binding.placeHolder.visibility = View.GONE
        binding.progressBarScreen.visibility = View.VISIBLE
    }

    private fun showError(errorMessage: String) {
        binding.recyclerTrackList.visibility = View.GONE
        binding.placeHolder.visibility = View.VISIBLE
        binding.progressBarScreen.visibility = View.GONE
        binding.searchPlaceholderText.text = errorMessage
        searchViewModel.clearTrackList()
        hideHistoryScreen()
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
    }

    private fun showContent(trackList: ArrayList<Track>) {
        binding.recyclerTrackList.visibility = View.VISIBLE
        binding.placeHolder.visibility = View.GONE
        binding.progressBarScreen.visibility = View.GONE
        searchViewModel.updateTrackList(trackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE, searchText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        searchText = savedInstanceState?.getString(SEARCH_LINE).toString()
    }

    companion object {
        const val SEARCH_LINE = "SEARCH LINE"
        const val TRACK_FOR_PLAYER = "TRACK FOR PLAYER"
        const val CLICK_DEBOUNCE_DELAY_MILLIS = 1_000L
    }
}