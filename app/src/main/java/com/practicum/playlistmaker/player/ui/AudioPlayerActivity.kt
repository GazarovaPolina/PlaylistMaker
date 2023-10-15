package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.MediaPlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var viewModel: MediaPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = IntentCompat.getParcelableExtra(intent, TRACK, Track::class.java)

        viewModel = ViewModelProvider(
            this,
            MediaPlayerViewModel.getViewModelFactory(track = track!!)
        )[MediaPlayerViewModel::class.java]

        viewModel.playerState.observe(this) {
            render(it)
        }

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            viewModel.playerStop()
            finish()
        }

        viewModel.preparePlayer()

        displayTrackInfo(track)

        binding.playPauseBtn.setOnClickListener {
            viewModel.playPauseControl()
        }
    }

    companion object {
        private const val TIME_FORMAT = "mm:ss"
        private const val TRACK = "track"
        private const val IMAGE_QUALITY = "512x512bb.jpg"
    }

    private fun displayTrackInfo(track: Track) {
        binding.trackName.text = track.trackName.toString()
        binding.artistName.text = track.artistName.toString()
        binding.trackDuration.text = SimpleDateFormat(
            TIME_FORMAT,
            Locale.getDefault()
        ).format(track.trackTime)
        binding.trackCollection.text = track.collectionName.toString()
        binding.trackReleaseDate.text = track.releaseDate?.subSequence(0, 4).toString()
        binding.trackGenre.text = track.primaryGenreName.toString()
        binding.trackCountry.text = track.country.toString()

        val trackImageUrl = track.artworkUrl100?.replaceAfterLast('/', IMAGE_QUALITY).toString()

        Glide.with(applicationContext)
            .load(trackImageUrl)
            .placeholder(R.drawable.track_placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)))
            .into(binding.trackImage)
    }

    private fun preparePlayer() {
       // viewModel.preparePlayer()
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
        binding.trackPlaybackProgress.text = getString(R.string.track_playback_progress)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun setPauseBtn(playBackProgress: String) {
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_pause)
        binding.trackPlaybackProgress.text = playBackProgress
    }

    private fun setPlayBtn() {
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
    }

    private fun render(state: MediaPlayerActivityState) {
        when (state) {
            is MediaPlayerActivityState.playerPreparedState -> preparePlayer()
            is MediaPlayerActivityState.playerPlayState -> setPauseBtn(state.playTime)
            is MediaPlayerActivityState.playerPauseState -> setPlayBtn()
        }
    }
}










