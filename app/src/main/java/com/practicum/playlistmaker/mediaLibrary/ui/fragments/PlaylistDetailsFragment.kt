package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.TrackInPlaylistAdapter
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.player.ui.CountMessageEndingChanger
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val bottomSheetTrackAdapter = TrackInPlaylistAdapter()
    private var bottomSheetTracksBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var confirmDeleteTrackDialog: MaterialAlertDialogBuilder? = null
    private var bottomSheetMenuBehavior: BottomSheetBehavior<LinearLayout>? = null

    val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(
            requireArguments().getString(
                ARGS_ID
            )
        )
    }

    private var currentPlaylist :Playlist? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playlistDetails.observe(viewLifecycleOwner) {
            renderPlaylistNameAndDescription(it)
        }

        viewModel.tracksDuration.observe(viewLifecycleOwner) {
            binding.playlistDetailsTracksDuration.text = CountMessageEndingChanger().getMinutesMessageEnding(it)
        }

        binding.toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.moreActionsWithPlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.recyclerViewPlaylistTracksBottomSheet.adapter = bottomSheetTrackAdapter

        viewModel.playlistTracks.observe(viewLifecycleOwner) {
            renderTrackListBottomSheet(it)
        }

        bottomSheetTrackAdapter.onTrackItemClick = {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
            intent.putExtra(TRACK, it)
            startActivity(intent)
        }

        bottomSheetTrackAdapter.onLongTrackItemClick = {
            deleteTrackFromPlaylist(it)
        }

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.moreActionsWithPlaylist.setOnClickListener {
            renderBottomSheetMenu()
            bottomSheetMenuBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomSheetSharePlaylist.setOnClickListener {
            sharePlaylist()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun sharePlaylist() {
        val tracks = bottomSheetTrackAdapter.tracks.toMutableList()
        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.playlist_without_tracks_message), Toast.LENGTH_SHORT).show()
        }
        else {
            viewModel.sharePlaylist(getPlaylistDetails())
        }
    }

    private fun renderPlaylistNameAndDescription(playlist: Playlist) {
        currentPlaylist = playlist
        binding.playlistDetailsName.text = playlist.playlistName
        binding.playlistDetailsDescription.text = playlist.playlistDescription
        binding.playlistDetailsTracksCount.text = CountMessageEndingChanger().getTracksCountMessageEnding(playlist.countTracks)
        Glide.with(requireContext()).load(playlist.imageUrl).placeholder(R.drawable.ic_playlist_cover_placeholder).centerCrop()
            .into(binding.playlistDetailsCover)
        Log.d("imageUrl", playlist.imageUrl.toString())
    }

    private fun renderBottomSheetMenu() {
        Log.d("imageUrl", currentPlaylist!!.imageUrl.toString())
        binding.bottomSheetPlaylistName.text = currentPlaylist!!.playlistName
        binding.bottomSheetCountPlaylistTracks.text = CountMessageEndingChanger().getTracksCountMessageEnding(currentPlaylist!!.countTracks)
        Glide
            .with(requireContext())
            .load(currentPlaylist!!.imageUrl)
            .placeholder(R.drawable.ic_playlist_cover_placeholder)
            .centerCrop()
            .into(binding.bottomSheetPlaylistImage)
    }

    private fun renderTrackListBottomSheet(tracksList: List<Track>) {
        if (tracksList.isNotEmpty()) {
            binding.playlistTracksBottomSheet.isVisible = true
            bottomSheetTrackAdapter.tracks.clear()
            bottomSheetTrackAdapter.tracks.addAll(tracksList)
            bottomSheetTrackAdapter.notifyDataSetChanged()
        } else {
            binding.playlistTracksBottomSheet.isVisible = false
            Toast.makeText(requireContext(), getString(R.string.tracks_count_in_playlist_message), Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteTrackFromPlaylist(trackToDelete: Track) {
        confirmDeleteTrackDialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_track)).setMessage(getString(R.string.confirm_delete))
                .setNeutralButton(getString(R.string.delete_track_confirm_dialog_neutral_button_message)) { dialog, which -> }
                .setPositiveButton(getString(R.string.delete_track_confirm_dialog_positive_button_message)) { dialog, which ->
                    viewModel.removeTrackFromPlaylist(
                        trackToDelete
                    )
                }
        confirmDeleteTrackDialog!!.show().apply {
            getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.blue, null))
            getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(resources.getColor(R.color.blue, null))
        }
    }

    private fun getPlaylistDetails(): String {
        val tracks = bottomSheetTrackAdapter.tracks.toMutableList()
        var playlistDescription = "${binding.playlistDetailsName.text}"

        if (binding.playlistDetailsDescription.text.isNotEmpty()) {
            playlistDescription += "\n${binding.playlistDetailsDescription.text}"
        }
        playlistDescription += "\n${binding.playlistDetailsTracksCount.text}"
        var tracksNumber = 1
        for (track in tracks) {
            playlistDescription += "\n$tracksNumber.${track.artistName}-${track.trackName}" +
                    "(${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)})"
            tracksNumber += 1

        }
        return playlistDescription
    }


    companion object {
        private const val ARGS_ID = "id"
        private const val TRACK = "track"
        fun createArgs(id: String): Bundle = bundleOf(ARGS_ID to id)
    }

}
