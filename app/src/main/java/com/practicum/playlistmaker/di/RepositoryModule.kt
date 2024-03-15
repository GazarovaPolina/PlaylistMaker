package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.impl.FavoritesRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.impl.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.favorites.FavoritesRepository
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.HistorySearchRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.HistorySearchRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
    }

    single<HistorySearchRepository> {
        HistorySearchRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    factory { TrackDbConverter() }

    factory { PlaylistDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }
}