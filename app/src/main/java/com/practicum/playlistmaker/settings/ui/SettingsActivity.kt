package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private var binding: ActivitySettingsBinding? = null

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.toolbarSettings?.setNavigationOnClickListener {
            finish()
        }

        viewModel.themeSwitcherState.observe(this) { isChecked ->
            binding?.themeSwitcher?.isChecked = isChecked
        }

        binding?.themeSwitcher?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
        }

        binding?.textViewShare?.setOnClickListener {
            viewModel.onShareAppLinkClicked()
        }

        binding?.textViewSupport?.setOnClickListener {
            viewModel.onWriteToSupportClicked()
        }

        binding?.textViewAgreement?.setOnClickListener {
           viewModel.onAgreementClicked()
        }
    }
}

