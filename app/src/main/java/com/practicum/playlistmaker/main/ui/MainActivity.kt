package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.screenState.observe(this) {
            showScreen(it)
        }

        binding?.btnSearch?.setOnClickListener {
            viewModel.onSearchBtnClicked()
        }

        binding?.btnMediaLib?.setOnClickListener {
            viewModel.onMediaLibBtnClicked()
        }

        binding?.btnSettings?.setOnClickListener {
            viewModel.onSettingsBtnClicked()
        }
    }

    private fun showScreen(chosenState: ScreenState) = when (chosenState) {
        ScreenState.Search -> {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        ScreenState.MediaLib -> {
            startActivity(Intent(this, MediaLibActivity::class.java))
        }

        ScreenState.Settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
