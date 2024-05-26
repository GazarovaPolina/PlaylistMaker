package com.practicum.playlistmaker.sharing.domain

interface SharingInteractor {

    fun share(text: String)
    fun openAgreement()
    fun writeToSupport()
}