package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    companion object {
        private const val DELAY_MILLIS = 10L
        private const val TIME_FORMAT = "mm:ss"
        private const val TRACK = "track"
        private const val IMAGE_QUALITY = "512x512bb.jpg"
    }

    private var playerState = MediaPlayerState.STATE_DEFAULT

    private var mainThreadHandler: Handler? = null
    private var mediaPlayer = MediaPlayer()
    private lateinit var trackPreviewUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        val track = IntentCompat.getParcelableExtra(intent, TRACK, Track::class.java)
        trackPreviewUrl = track?.previewUrl!!

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            finish()
        }

        displayTrackInfo(track)

        preparePlayer()

        binding.playPauseBtn.setOnClickListener {
            playbackControl()
            mainThreadHandler?.post(createUpdateTimerTask())
        }

        binding.toolbarAudioPlayer.setNavigationOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    private fun displayTrackInfo(track: Track) {
        binding.trackName.text = track?.trackName.toString()
        binding.artistName.text = track?.artistName.toString()
        binding.trackDuration.text = SimpleDateFormat(
            TIME_FORMAT,
            Locale.getDefault()
        ).format(track?.trackTime)
        binding.trackCollection.text = track?.collectionName.toString()
        binding.trackReleaseDate.text = track?.releaseDate?.subSequence(0, 4).toString()
        binding.trackGenre.text = track?.primaryGenreName.toString()
        binding.trackCountry.text = track?.country.toString()

        val trackImageUrl = track?.artworkUrl100?.replaceAfterLast('/', IMAGE_QUALITY).toString()

        Glide.with(applicationContext)
            .load(trackImageUrl)
            .placeholder(R.drawable.track_placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)))
            .into(binding.trackImage)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = MediaPlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
            playerState = MediaPlayerState.STATE_PREPARED
            binding.trackPlaybackProgress.text = "00:00"
            mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
        }
    }

    private fun playbackControl() {
        when (playerState) {
            MediaPlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
        playerState = MediaPlayerState.STATE_PAUSED
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_pause)
        playerState = MediaPlayerState.STATE_PLAYING
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerState) {
                    MediaPlayerState.STATE_PLAYING -> {
                        binding.trackPlaybackProgress.text = SimpleDateFormat(
                            TIME_FORMAT,
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
                    }

                    MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                        mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
                    }

                    else -> {}
                }
            }
        }
    }

}

enum class MediaPlayerState(val state: Int) {
    STATE_DEFAULT(0),
    STATE_PREPARED(1),
    STATE_PLAYING(2),
    STATE_PAUSED(3)
}