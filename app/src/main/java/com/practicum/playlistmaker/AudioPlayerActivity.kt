package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    companion object {
        private const val DELAY = 500L
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
        trackPreviewUrl = intent.extras?.getString("previewUrl") ?: ""


        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            finish()
        }

        displayTrackInfo()

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

    private fun displayTrackInfo() {
        binding.trackName.text = intent.extras?.getString("trackName") ?: "-"
        binding.artistName.text = intent.extras?.getString("artistName") ?: "-"
        binding.trackDuration.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(intent.extras?.getLong("trackTime")) ?: "-"
        binding.trackCollection.text = intent.extras?.getString("collectionName") ?: "-"
        binding.trackReleaseDate.text =
            intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: "-"
        binding.trackGenre.text = intent.extras?.getString("primaryGenreName") ?: "-"
        binding.trackCountry.text = intent.extras?.getString("country") ?: "-"

        val trackImageUrl =
            intent.extras?.getString("artworkUrl100")?.replaceAfterLast('/', "512x512bb.jpg")

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
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, DELAY)
                    }

                    MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                        mainThreadHandler?.postDelayed(this, DELAY)
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