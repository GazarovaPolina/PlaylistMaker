package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.MainViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibFavoritesViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibPlaylistsViewModel
import com.practicum.playlistmaker.player.ui.MediaPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel {
        MainViewModel()
    }

    viewModel { (track: Track) ->
        MediaPlayerViewModel(get(), track)
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        MediaLibFavoritesViewModel()
    }

    viewModel {
        MediaLibPlaylistsViewModel()
    }
}