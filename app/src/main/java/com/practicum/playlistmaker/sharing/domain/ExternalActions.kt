package com.practicum.playlistmaker.sharing.domain

interface ExternalActions {

    fun shareAppLink(link: String)
    fun openAgreement(link: String)
    fun writeToSupport(emailAddress: String, emailSubject: String, emailText: String)
}