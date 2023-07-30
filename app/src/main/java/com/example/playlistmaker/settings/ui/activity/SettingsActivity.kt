package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewModel = SettingsViewModel.getViewModelFactory().create(SettingsViewModel::class.java)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            viewModel.shareApp(getString(R.string.share_link))
        }

        binding.btnSupport.setOnClickListener {
            viewModel.contactSupport(
                getString(R.string.support_email),
                getString(R.string.support_email_title),
                getString(R.string.support_message)
            )
        }

        binding.btnLegal.setOnClickListener {
            viewModel.openLegal(getString(R.string.legal_link))
        }

        viewModel.themeSettingsLiveData.observe(this) { themeToggle ->
            binding.themeSwitcher.isChecked = themeToggle.darkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.updateThemeSettings(checked)
        }

    }

}