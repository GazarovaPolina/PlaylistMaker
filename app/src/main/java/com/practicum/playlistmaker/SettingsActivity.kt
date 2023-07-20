package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


fun switchTheme(darkThemeEnabled: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (darkThemeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    )
}


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        binding.textViewShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = getString(R.string.intent_type)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(R.string.start_activity_share_title)
                )
            )
        }

        binding.textViewSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, R.string.support_email_address)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            }
            startActivity(
                Intent.createChooser(
                    emailIntent,
                    getString(R.string.start_activity_support_title)
                )
            )
        }

        binding.textViewAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_link))
            val openLinkIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(openLinkIntent)
        }

        val sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        binding.themeSwitcher.isChecked = isNightModeOn()


        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            switchTheme(checked)
            sharedPrefs.edit {
                putBoolean(THEME_KEY, checked)
            }
        }
    }

    private fun isNightModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}