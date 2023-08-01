package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_LINE = "SEARCH LINE"
        const val TRACK_FOR_PLAYER = "TRACK FOR PLAYER"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var tracksSearchViewModel: SearchViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    private var searchText: String = ""
    private val trackList = ArrayList<Track>()
    private var searchHistoryList = ArrayList<Track>()

    private val trackAdapter = TrackAdapter(trackList) {
        tracksSearchViewModel.putTrackToHistory(it)
        if (clickDebounce()) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(PlayerActivity.TRACK_FOR_PLAYER, it)
            startActivity(playerIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tracksSearchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        binding.trackList.adapter = trackAdapter

        tracksSearchViewModel.stateObserver().observe(this) {
            render(it)
        }

        tracksSearchViewModel.fillHistory()

        tracksSearchViewModel.historyLiveData.observe(this) { historyTrackList ->
            searchHistoryList = historyTrackList
        }

        binding.inputSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.inputSearch.text.isEmpty() && searchHistoryList.isNotEmpty()) {
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
                tracksSearchViewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                if (!s.isNullOrEmpty()) {
                    binding.trackList.visibility = View.VISIBLE
                    trackList.clear()
                    trackAdapter.trackList = trackList
                    hideHistoryScreen()
                }

                tracksSearchViewModel.fillHistory()

                if (s?.isEmpty() == true && searchHistoryList.isNotEmpty()) {
                    showHistoryScreen()
                } else {
                    binding.trackList.visibility = View.GONE
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
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)

            if (searchHistoryList.isNotEmpty()) {
                showHistoryScreen()
            } else {
                hideHistoryScreen()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.placeholderRefreshButton.setOnClickListener {
            tracksSearchViewModel.searchTrack(searchText)
        }

        binding.historyClear.setOnClickListener {
            hideHistoryScreen()
            binding.trackList.visibility = View.VISIBLE
            trackAdapter.notifyDataSetChanged()
            tracksSearchViewModel.clearHistory()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.inputSearch.removeTextChangedListener(it) }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.Success -> showContent(state.data)
            is SearchScreenState.Error -> {
                showError(state.message)
                binding.searchPlaceholderText.setText(R.string.check_network)
                binding.searchPlaceHolderImage.setImageResource(R.drawable.no_internet)
                binding.placeholderRefreshButton.visibility = View.VISIBLE
            }

            is SearchScreenState.Empty -> {
                showEmpty(state.message)
                binding.searchPlaceholderText.setText(R.string.nothing_found)
                binding.searchPlaceHolderImage.setImageResource(R.drawable.nothing_found)
                binding.placeholderRefreshButton.visibility = View.GONE
            }
        }
    }

    private fun showHistoryScreen() {
        binding.searchHistory.visibility = View.VISIBLE
        binding.historyClear.visibility = View.VISIBLE
        binding.historyTrackList.visibility = View.VISIBLE
        binding.placeHolder.visibility = View.GONE
        trackAdapter.trackList = searchHistoryList
        trackAdapter.notifyDataSetChanged()
    }

    private fun hideHistoryScreen() {
        binding.searchHistory.visibility = View.GONE
        binding.historyClear.visibility = View.GONE
    }

    private fun showLoading() {
        binding.trackList.visibility = View.GONE
        binding.placeHolder.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError(errorMessage: String) {
        binding.trackList.visibility = View.GONE
        binding.placeHolder.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchPlaceholderText.text = errorMessage
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        hideHistoryScreen()
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
    }

    private fun showContent(trackList: List<Track>) {
        binding.trackList.visibility = View.VISIBLE
        binding.placeHolder.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
        binding.historyClear.visibility = View.GONE
        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_LINE).toString()
    }

}