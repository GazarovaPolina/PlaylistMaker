package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibFavoritesBinding
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibFavoritesState
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.MediaLibFavoritesViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibFavoritesFragment : Fragment() {

    private var _binding: FragmentMediaLibFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaLibFavoritesViewModel by viewModel()

    private val trackAdapter = TrackAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaLibFavoritesBinding.inflate(inflater, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaLibFavoritesPlaceholderMessage.text = requireArguments().getString(PLACEHOLDER_TEXT).toString()
        viewModel.refreshFavorites()

        viewModel.observeState().observe(viewLifecycleOwner) {
            executeAction(it)
        }

        trackAdapter.onItemClick = {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
            intent.putExtra(TRACK, it)
            startActivity(intent)
        }
    }

    override fun onResume() {
        viewModel.refreshFavorites()
        super.onResume()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun executeAction(state: MediaLibFavoritesState) {
        binding.mediaLibFavoritesPlaceholderImage.isVisible = state is MediaLibFavoritesState.Empty
        binding.mediaLibFavoritesPlaceholderMessage.isVisible = state is MediaLibFavoritesState.Empty
        binding.recyclerViewFavoritesTracks.isVisible = state is MediaLibFavoritesState.Content

        if (state is MediaLibFavoritesState.Content) {
            binding.recyclerViewFavoritesTracks.adapter = trackAdapter
            trackAdapter.tracks.clear()
            trackAdapter.tracks.addAll(state.tracks)
            trackAdapter.notifyDataSetChanged()

        }
    }

    companion object {
        private const val PLACEHOLDER_TEXT = "placeholderText"
        private const val TRACK = "track"

        fun newInstance(placeholderText: String) = MediaLibFavoritesFragment().apply {
            arguments = Bundle().apply {
                putString(PLACEHOLDER_TEXT, placeholderText)
            }
        }
    }
}