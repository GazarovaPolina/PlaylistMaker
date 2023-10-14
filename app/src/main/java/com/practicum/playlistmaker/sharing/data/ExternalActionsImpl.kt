package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.ExternalActions


class ExternalActionsImpl(private val context: Context) : ExternalActions {

    override fun shareAppLink(link: String) {

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = context.getString(R.string.intent_type)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_link))
        }
        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openAgreement(link: String) {
        val url = Uri.parse(context.getString(R.string.user_agreement_link))
        val openLinkIntent = Intent(Intent.ACTION_VIEW, url)
        context.startActivity(openLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun writeToSupport(emailAddress: String, emailSubject: String, emailText: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.mail_to))
            putExtra(Intent.EXTRA_EMAIL, R.string.support_email_address)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_email_text))
        }
        /*context.startActivity(
            Intent.createChooser(
                emailIntent,
                context.getString(R.string.start_activity_support_title)
            )
        )*/

        context.startActivity((emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
    }
}