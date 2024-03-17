package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.player.domain.MediaPlayerActivityState
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        binding.toolbarAudioPlayer.setNavigationOnClickListener {
            viewModel.playerStop()
            finish()
        }

        viewModel.preparePlayer()

        displayTrackInfo(track)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.linearLayoutBottomSheet).apply {

                state = BottomSheetBehavior.STATE_HIDDEN

        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

//        bottomSheetBehavior = BottomSheetBehavior.from(binding.linearLayoutBottomSheet).apply {
//            state = BottomSheetBehavior.STATE_HIDDEN
//        }

        binding.playPauseBtn.setOnClickListener {
            viewModel.playPauseControl()
        }

        binding.addToFavoritesBtn.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.addToPlaylistBtn.setOnClickListener {
//         binding.overlay.visibility = View.VISIBLE
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
////            viewModel.getPlayLists()
////            viewModel.playlistState.observe(this) { list ->
////               // bottomSheetAdapter.
////                binding.recyclerViewBottomSheet.adapter = bottomSheetAdapter
////            }
//
//
//
//            viewModel.refreshBottomSheet()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            Log.d("aaaaa", bottomSheetBehavior.state.toString())
        }

//        binding.buttonCreateNewPlaylistBottomSheet.setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container_view, CreatePlaylistFragment())
//                .addToBackStack("playerActivity")
//                .commit()
//
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        }

        bottomSheetAdapter.onItemClick = {
            viewModel.addTrackToPlaylist(it, track.trackId)
            //Toast.makeText(this, "Добавлено в плейлист ${it.playlistName}.", Toast.LENGTH_SHORT)
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.playlistState.observe(this) {
            if (it != null) {
                updateBottomSheetAdapter(it)
            }
        }

        viewModel.trackState.observe(this) {
            updatePlaylistMessage(it)
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

    private fun updatePlaylistMessage(trackInPlaylistState: String) {
        Toast.makeText(this, trackInPlaylistState, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TIME_FORMAT = "mm:ss"
        private const val TRACK = "track"
        private const val IMAGE_QUALITY = "512x512bb.jpg"
    }
}










