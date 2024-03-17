package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.MainViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.MediaLibFavoritesViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.MediaLibPlaylistsViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.NewPlaylistCreationModel
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
        MediaPlayerViewModel(track, get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        MediaLibFavoritesViewModel(get())
    }

    viewModel {
        NewPlaylistCreationModel(get())
    }

    viewModel {
        MediaLibPlaylistsViewModel(get())
    }
}