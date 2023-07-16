package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbarSettings)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val switchCompatMode = findViewById<SwitchCompat>(R.id.themeSwitcher)

        if (isNightModeOn()) {
            switchCompatMode.isChecked = true
        }

        val textViewShare = findViewById<TextView>(R.id.textViewShare)
        textViewShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = getString(R.string.intent_type)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.start_activity_share_title)))
        }

        val textViewSupport = findViewById<TextView>(R.id.textViewSupport)
        textViewSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, R.string.support_email_address)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.start_activity_support_title)))
        }

        val textViewAgreement = findViewById<TextView>(R.id.textViewAgreement)
        textViewAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_link))
            val openLinkIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(openLinkIntent)
        }


        switchCompatMode.setOnCheckedChangeListener { _, checkedId ->
            AppCompatDelegate.setDefaultNightMode(if (checkedId) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun isNightModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}