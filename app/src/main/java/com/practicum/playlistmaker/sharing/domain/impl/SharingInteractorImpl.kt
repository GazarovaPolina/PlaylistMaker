package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.ExternalActions
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val actions: ExternalActions
) : SharingInteractor {

    override fun shareAppLink() {
        actions.shareAppLink("https://practicum.yandex.ru/android-developer/")
    }

    override fun openAgreement() {
        actions.openAgreement("https://yandex.ru/legal/practicum_offer/")
    }

    override fun writeToSupport() {
        actions.writeToSupport(
            "polinagazarova@yandex.ru",
            "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
    }
}