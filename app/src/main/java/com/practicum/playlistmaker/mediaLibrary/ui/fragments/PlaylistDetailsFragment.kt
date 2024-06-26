package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import kotlinx.coroutines.runBlocking
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
    private var confirmDeletePlaylistDialog: MaterialAlertDialogBuilder? = null
    private var playlist: Playlist? = null

    val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(
            requireArguments().getString(
                ARGS_ID
            )
        )
    }

    private var currentPlaylist: Playlist? = null


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
            playlist = it
            renderBottomSheetMenu()
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
            bottomSheetMenuBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomSheetSharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.bottomSheetDeletePlaylist.setOnClickListener {
            bottomSheetMenuBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            playlist?.let { deletePlaylist(it) }
        }

        binding.bottomSheetEditPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                requireArguments().getString(ARGS_ID)?.let { EditPlaylistFragment.createArgs(it) })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.renderPlaylist()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun sharePlaylist() {
        val tracks = bottomSheetTrackAdapter.tracks.toMutableList()
        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.playlist_without_tracks_message), Toast.LENGTH_SHORT).show()
        } else {
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
    }

    private fun renderBottomSheetMenu() {
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
            bottomSheetTrackAdapter.tracks.clear()
            bottomSheetTrackAdapter.tracks.addAll(tracksList)
            bottomSheetTrackAdapter.notifyDataSetChanged()
            binding.tracksCountMessage.isVisible = false
            binding.recyclerViewPlaylistTracksBottomSheet.isVisible = true
        } else {
            binding.tracksCountMessage.isVisible = true
            binding.recyclerViewPlaylistTracksBottomSheet.isVisible = false

        }
    }


    private fun deleteTrackFromPlaylist(trackToDelete: Track) {
        confirmDeleteTrackDialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_track))
                .setMessage(getString(R.string.confirm_delete))
                .setNegativeButton(getString(R.string.delete_track_confirm_dialog_neutral_button_message)) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.delete_track_confirm_dialog_positive_button_message)) { dialog, which ->
                    viewModel.removeTrackFromPlaylist(trackToDelete)
                }
        confirmDeleteTrackDialog!!.show().apply {
            getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.blue, null))
            getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.blue, null))
        }
    }

    private fun deletePlaylist(playlistToDelete: Playlist) {
        confirmDeletePlaylistDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_playlist))
                .setMessage(getString(R.string.delete_playlist_confirm_message, playlistToDelete.playlistName))
                .setNegativeButton(getString(R.string.delete_playlist_confirm_dialog_neutral_button_message)) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.delete_playlist_confirm_dialog_positive_button_message)) { dialog, which ->
                    runBlocking {
                        viewModel.removePlaylist(playlistToDelete)
                        findNavController().popBackStack()
                    }

                }

        confirmDeletePlaylistDialog!!.show().apply {
            getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.blue, null))
            getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.blue, null))
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
