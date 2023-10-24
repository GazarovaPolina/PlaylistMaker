package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.ExternalActions
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val actions: ExternalActions
) : SharingInteractor {

    override fun shareAppLink() {
        actions.shareAppLink()
    }

    override fun openAgreement() {
        actions.openAgreement()
    }

    override fun writeToSupport() {
        actions.writeToSupport()
    }
}