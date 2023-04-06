package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbarSettings)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val textViewShare = findViewById<TextView>(R.id.textViewShare)
        textViewShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = getString(R.string.IntentType)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ShareLink))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.StartActivityShareTitle)))
        }

        val textViewSupport = findViewById<TextView>(R.id.textViewSupport)
        textViewSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(getString(R.string.MailTo))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, R.string.SupportEmailSubject)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.SupportEmailSubject))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.SupportEmailText))
            startActivity(Intent.createChooser(emailIntent, getString(R.string.StartActivitySupportTitle)))
        }

        val textViewAgreement = findViewById<TextView>(R.id.textViewAgreement)
        textViewAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.userAgreementLink))
            val openLinkIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(openLinkIntent)
        }

    }

}