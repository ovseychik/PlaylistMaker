package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_LINE = "SEARCH_LINE"
    }

    private lateinit var textSearch: EditText
    private var searchTextString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        textSearch = findViewById(R.id.inputSearch)
        val btnClearText = findViewById<ImageView>(R.id.clearTextSearch)

        btnClearText.setOnClickListener {
            textSearch.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(textSearch.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchTextString = textSearch.text.toString()
                btnClearText.visibility = visibilityView(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        textSearch.addTextChangedListener(searchTextWatcher)

        val buttonBack = findViewById<ImageView>(R.id.btn_back)
        buttonBack.setOnClickListener {
            //val backIntent = Intent(this, MainActivity::class.java)
            finish()
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
        textSearch.setText(savedInstanceState.getString(SEARCH_LINE))
    }
}

