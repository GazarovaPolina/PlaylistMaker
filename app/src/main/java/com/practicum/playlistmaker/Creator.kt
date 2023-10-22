package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.data.SearchDebounceImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.HistorySearchInteractorImpl
import com.practicum.playlistmaker.search.data.HistorySearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistorySearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchDebounce
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.ExternalActionsImpl
import com.practicum.playlistmaker.sharing.domain.ExternalActions
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    lateinit var app: Application

    fun initApp(application: Application) {
        this.app = application
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(app))
    }

    private fun getSettingsRepository(app: Application): SettingsRepository {
        return SettingsRepositoryImpl(app)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalActions(app))
    }

    private fun getExternalActions(context: Context): ExternalActions {
        return ExternalActionsImpl(context)
    }


    fun provideMediaPlayerInteractor(trackPreviewUrl: String): MediaPlayerInteractorImpl {
        return MediaPlayerInteractorImpl(MediaPlayerRepositoryImpl(trackPreviewUrl = trackPreviewUrl))
    }

    fun provideSearchHistory(): HistorySearchInteractor {
        return HistorySearchInteractorImpl(HistorySearchRepositoryImpl(app))
    }

    fun getSearchDebounce(): SearchDebounce  {
        return SearchDebounceImpl()
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TrackInteractorImpl(TracksRepositoryImpl(RetrofitNetworkClient(context = app)))
    }




}