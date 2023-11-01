package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.themeSettingsLiveData.observe(viewLifecycleOwner) { themeToggle ->
            binding.themeSwitcher.isChecked = themeToggle.darkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.updateThemeSettings(checked)
        }

    }
}

