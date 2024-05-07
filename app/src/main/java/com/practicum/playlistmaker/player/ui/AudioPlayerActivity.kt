package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.fragments.NewPlaylistFragment
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
import com.practicum.playlistmaker.player.domain.TrackInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    private val track by lazy { IntentCompat.getParcelableExtra(intent, TRACK, Track::class.java)!! }

    private val viewModel: MediaPlayerViewModel by viewModel { parametersOf(track) }

    private val bottomSheetAdapter = BottomSheetAdapter()
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.mediaPlayerState.observe(this) {
            render(it)
        }

        viewModel.favoritesState.observe(this) {
            renderFavoriteTrackState(it)
        }

        viewModel.trackState.observe(this) {
            if (it is TrackInPlaylistState.TrackNotInPlaylist) {
                hideBottomSheet()
            }

            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            viewModel.playerStop()
            finish()
        }

        viewModel.preparePlayer()

        displayTrackInfo(track)

        hideBottomSheet()

        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.shading.isVisible = false

                    }

                    else -> {
                        binding.shading.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.recyclerViewBottomSheet.adapter = bottomSheetAdapter

        viewModel.playlistState.observe(this) { playlistList ->
            if (playlistList != null) {
                bottomSheetAdapter.playlists.clear()
                bottomSheetAdapter.playlists.addAll(playlistList.toList())
                bottomSheetAdapter.notifyDataSetChanged()
            }
        }

        binding.playPauseBtn.setOnClickListener {
            viewModel.playPauseControl()
        }

        binding.addToFavoritesBtn.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }


        binding.addToPlaylistBtn.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.getPlayLists()
        }

        bottomSheetAdapter.onItemClick = {
            viewModel.addTrackToPlaylist(it, track.trackId)
        }


        viewModel.playlistState.observe(this) { playlistList ->
            if (playlistList != null) {
                updateBottomSheetAdapter(playlistList)
            }
        }

        binding.buttonCreateNewPlaylistBottomSheet.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.newPlaylistFragmentContainerView, NewPlaylistFragment())
                .addToBackStack("AudioPlayerActivity")
                .commit()

            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }
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
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun setPauseBtn() {
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_pause)
    }

    private fun setPlayBtn() {
        binding.playPauseBtn.setBackgroundResource(R.drawable.ic_play)
    }

    private fun render(state: MediaPlayerActivityState) {
        binding.trackPlaybackProgress.text = state.playTime ?: getString(R.string.track_playback_progress)
        when (state) {
            is MediaPlayerActivityState.PlayerPreparedState -> preparePlayer()
            is MediaPlayerActivityState.PlayerPlayState -> setPauseBtn()
            is MediaPlayerActivityState.PlayerPauseState -> setPlayBtn()
        }
    }

    private fun renderFavoriteTrackState(trackFavoriteState: Boolean) {
        if (trackFavoriteState) {
            binding.addToFavoritesBtn.setBackgroundResource(R.drawable.ic_delete_from_favorites)
        } else {
            binding.addToFavoritesBtn.setBackgroundResource(R.drawable.ic_add_to_favorites)
        }
    }

    private fun updateBottomSheetAdapter(playlists: List<Playlist>) {
        bottomSheetAdapter.playlists.clear()
        bottomSheetAdapter.playlists.addAll(playlists)
        bottomSheetAdapter.notifyDataSetChanged()
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.addToPlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    companion object {
        private const val TIME_FORMAT = "mm:ss"
        private const val TRACK = "track"
        private const val IMAGE_QUALITY = "512x512bb.jpg"
    }
}










