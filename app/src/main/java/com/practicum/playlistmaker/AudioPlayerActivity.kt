package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            finish()
        }

        binding.trackName.text = intent.extras?.getString("trackName") ?: "-"
        binding.artistName.text = intent.extras?.getString("artistName") ?: "-"
        binding.trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(intent.extras?.getLong("trackTime")) ?: "-"
        binding.trackCollection.text = intent.extras?.getString("collectionName") ?: "-"
        binding.trackReleaseDate.text = intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: "-"
        binding.trackGenre.text = intent.extras?.getString("primaryGenreName") ?: "-"
        binding.trackCountry.text = intent.extras?.getString("country") ?: "-"

        val trackImageUrl = intent.extras?.getString("artworkUrl100")?.replaceAfterLast('/', "512x512bb.jpg")

        Glide.with(applicationContext)
            .load(trackImageUrl)
            .placeholder(R.drawable.track_placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)))
            .into(binding.trackImage)
    }
}