package com.example.playlistmaker.search.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.TrackResponse
import com.example.playlistmaker.player.ui.PlayerActivity
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_LINE = "SEARCH LINE"
        const val SEARCH_PREFERENCES = "SEARCH_PREFERENCES"
        const val TRACK_FOR_PLAYER = "TRACK_FOR_PLAYER"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }

    private lateinit var queryInput: EditText
    private lateinit var placeholder: LinearLayout
    private lateinit var placeholderText: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderNoNetworkButton: Button
    private lateinit var trackRecycler: RecyclerView
    private lateinit var historyClearTracks: Button
    private lateinit var searchSharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var historyTrackRecycler: RecyclerView
    private lateinit var historySearched: LinearLayout
    private lateinit var buttonBack: ImageView
    private lateinit var progressBarScreen: LinearLayout
    private lateinit var progressBar: ProgressBar

    // Debounce поискового запроса
    private val searchRunnable = Runnable { searchTrack() }

    // Debounce для нажатий пользователя
    private var isClickAllowed = true
    private var handler = Handler(Looper.getMainLooper())

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private var searchTextString = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = ArrayList<Track>()

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.inputSearch)
        placeholder = findViewById(R.id.placeHolder)
        placeholderText = findViewById(R.id.searchPlaceholderText)
        placeholderImage = findViewById(R.id.searchPlaceHolderImage)
        searchSharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(searchSharedPrefs, gson)
        historyClearTracks = findViewById(R.id.historyClear)
        historySearched = findViewById(R.id.searchHistory)
        historyTrackRecycler = findViewById(R.id.historyTrackList)
        trackRecycler = findViewById(R.id.trackList)
        progressBarScreen = findViewById(R.id.progressBarScreen)
        progressBar = findViewById(R.id.progressBar)

        // Кнопка очистки строки поиска
        val btnClearText = findViewById<ImageView>(R.id.clearTextSearch)
        btnClearText.setOnClickListener {
            queryInput.setText("")
            flushView()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }

        historyAdapter = TrackAdapter { it ->
            startPlayerActivity(it)
        }

        historyTrackRecycler.adapter = historyAdapter
        historyTrackRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        refreshHistory()

        historyClearTracks.setOnClickListener() {
            searchHistory.clearHistory()
            historySearched.visibility = View.GONE
            refreshHistory()
        }

        adapter = TrackAdapter {
            searchHistory.addTrack(it)
            startPlayerActivity(it)
            refreshHistory()
        }

        trackRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecycler.adapter = adapter
        adapter.tracks = tracks

        // Кнопка обновить при ошибке сети
        placeholderNoNetworkButton = findViewById(R.id.placeholderRefreshButton)
        placeholderNoNetworkButton.setOnClickListener {
            searchTrack()
        }

        queryInput.setOnFocusChangeListener() { view, hasFocus ->
            historySearched.visibility =
                if (hasFocus && queryInput.text.isEmpty() && searchHistory.getHistory().isNotEmpty()
                ) {
                    placeholder.visibility = View.GONE
                    refreshHistory()
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        // по нажитию Enter на клавиатуре.
        // В постановке не было явно указано убрать. Оставил для нетерпеливых.
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }


        // инициализируем recyclerview

        adapter.tracks = tracks
        trackRecycler.layoutManager = LinearLayoutManager(this)
        trackRecycler.adapter = adapter

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                seq: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(seq: CharSequence?, start: Int, count: Int, after: Int) {
                historySearched.visibility =
                    if (queryInput.hasFocus() && seq?.isEmpty() == true
                        && searchHistory.getHistory()?.isEmpty() == false
                    ) View.VISIBLE else View.GONE
                searchTextString = queryInput.text.toString()
                btnClearText.visibility = visibilityView(seq)
                searchDebounce()
            }

            override fun afterTextChanged(start: Editable?) {}
        }

        queryInput.addTextChangedListener(searchTextWatcher)

        buttonBack = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            finish()
        }
    }

    //Обработка ошибок через Enum
    private enum class Results {
        CHECK_NETWORK,
        NOTHING_FOUND
    }

    private fun showPlaceholder(result: Results) {
        placeholder.visibility = View.VISIBLE
        if (result == Results.NOTHING_FOUND) {
            placeholderImage.setImageResource(R.drawable.nothing_found)
            placeholderText.text = getString(R.string.nothing_found)
        } else if (result == Results.CHECK_NETWORK) {
            placeholderImage.setImageResource(R.drawable.no_internet)
            placeholderText.text = getString(R.string.check_network)
            placeholderNoNetworkButton.text = getString(R.string.refresh_button_text)
            placeholderNoNetworkButton.visibility = View.VISIBLE
        }
    }

    fun visibilityView(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE, searchTextString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        queryInput.setText(savedInstanceState.getString(SEARCH_LINE))
    }

    private fun searchTrack() {
        flushView()
        if (queryInput.text.isNotEmpty()) {
            progressBarScreen.visibility = View.VISIBLE
            itunesService.search(queryInput.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        progressBarScreen.visibility = View.GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showPlaceholder(Results.NOTHING_FOUND)
                            }
                        }

                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showPlaceholder(Results.CHECK_NETWORK)
                    }
                })

        }
    }

    private fun refreshHistory() {
        if (searchHistory.getHistory()?.isEmpty() == true) {
            historyAdapter.notifyDataSetChanged()
            return
        }
        historyAdapter.tracks = searchHistory.getHistory() as ArrayList<Track>
        historyAdapter.notifyDataSetChanged()
    }

    // Реализация Debounce
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(TRACK_FOR_PLAYER, track)
            startActivity(intent)
        }
    }

    //Очистка экрана от лишних view при новом поиске: плейсхолдеры и результаты поиска
    private fun flushView() {
        placeholder.visibility = View.GONE
        // Это если попадем сюда после плейсхолдера "Нет сети"
        placeholderNoNetworkButton.visibility = View.GONE
        tracks.clear()
        adapter.notifyDataSetChanged()
    }

}