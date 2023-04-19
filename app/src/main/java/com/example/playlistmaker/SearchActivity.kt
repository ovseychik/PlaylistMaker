package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_LINE = "SEARCH LINE"
        const val CHECK_NETWORK = "CHECK NETWORK"
        const val TRACK_NOT_FOUND = "NO RESULT"
    }

    private lateinit var queryInput: EditText
    private lateinit var placeholderNoNetwork: LinearLayout
    private lateinit var placeholderNoNetworkButton: Button
    private lateinit var placeholderNoResult: LinearLayout

    private var searchTextString = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.inputSearch)
        placeholderNoNetwork = findViewById(R.id.placeHolderNoInternet)
        placeholderNoResult = findViewById(R.id.placeHolderNothingFound)

        val btnClearText = findViewById<ImageView>(R.id.clearTextSearch)

        // Действие по кнопке очистки строки поиска
        btnClearText.setOnClickListener {
            queryInput.setText("")
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }

        // Кнопка обновить при ошибке сети
        placeholderNoNetworkButton = findViewById(R.id.placeHolderNoInternetRefreshButton)
        placeholderNoNetworkButton.setOnClickListener {
            searchTrack()
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        // инициализируем recyclerview
        val trackRecycler = findViewById<RecyclerView>(R.id.trackList)

        adapter.tracks = tracks
        trackRecycler.layoutManager = LinearLayoutManager(this)
        trackRecycler.adapter = adapter

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                seq: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(seq: CharSequence?, start: Int, count: Int, after: Int) {
                searchTextString = queryInput.text.toString()
                btnClearText.visibility = visibilityView(seq)
            }

            override fun afterTextChanged(start: Editable?) {}
        }

        queryInput.addTextChangedListener(searchTextWatcher)

        val buttonBack = findViewById<ImageView>(R.id.btn_back)
        buttonBack.setOnClickListener {
            finish()
        }
    }

    fun showResults(text: String) {
        when (text) {
            TRACK_NOT_FOUND -> placeholderNoResult.visibility = View.VISIBLE
            else -> placeholderNoNetwork.visibility = View.VISIBLE
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
        // Скроем сообщения об ошибках, если они были перед этим запуском поиска
        placeholderNoNetwork.visibility = View.GONE
        placeholderNoResult.visibility = View.GONE
        // И очистим список треков в RecyclerView, если поиск выполняется второй раз на экране
        tracks.clear()
        adapter.notifyDataSetChanged()

        if (queryInput.text.isNotEmpty()) {
            itunesService.search(queryInput.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showResults(TRACK_NOT_FOUND)
                            } else {
                                showResults(CHECK_NETWORK)
                            }
                        }

                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showResults(CHECK_NETWORK)
                    }
                })

        }
    }

}
