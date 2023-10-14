package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.ExternalActionsImpl
import com.practicum.playlistmaker.sharing.domain.ExternalActions
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private lateinit var app: Application

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


    fun getMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractor(MediaPlayerRepositoryImpl())
    }
}