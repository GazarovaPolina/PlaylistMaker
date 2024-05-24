package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.CountMessageEndingChanger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(
            requireArguments().getString(
                ARGS_ID
            )
        )
    }

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
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderPlaylistNameAndDescription(playlist: Playlist) {
        binding.playlistDetailsName.text = playlist.playlistName
        binding.playlistDetailsDescription.text = playlist.playlistDescription
        binding.playlistDetailsTracksCount.text = CountMessageEndingChanger().getTracksCountMessageEnding(playlist.countTracks)
        Glide.with(requireContext())
            .load(playlist.imageUrl)
            .placeholder(R.drawable.ic_playlist_cover_placeholder)
            .centerCrop()
            .into(binding.playlistDetailsCover)
    }


    companion object {
        private const val ARGS_ID = "id"
        fun createArgs(id: String): Bundle =
            bundleOf(ARGS_ID to id)

    }
}