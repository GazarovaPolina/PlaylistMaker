package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistsState
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.MediaLibPlaylistsViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibPlaylistsFragment : Fragment() {

    private var _binding: FragmentMediaLibPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaLibPlaylistsViewModel by viewModel()

    private lateinit var playlistsRecyclerView: RecyclerView
    private val playlistsAdapter = PlaylistAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaLibPlaylistsBinding.inflate(inflater, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaLibPlaylistsPlaceholderMessage.text = requireArguments().getString(PLACEHOLDER_TEXT).toString()
        viewModel.refreshPlaylists()

        binding.mediaLibNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment, null)
        }

        playlistsRecyclerView = binding.recyclerViewPlaylists
        playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsRecyclerView.adapter = playlistsAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            executeAction(it)
        }

        playlistsAdapter.onPlaylistItemClick = {
            findNavController().navigate(R.id.playlistDetailsFragment, PlaylistDetailsFragment.createArgs(it.id.toString()))
        }
    }

    override fun onResume() {
        viewModel.refreshPlaylists()
        super.onResume()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun executeAction(state: PlaylistsState) {
        binding.mediaLibFavoritesPlaceholderImage.isVisible = state is PlaylistsState.Empty
        binding.mediaLibPlaylistsPlaceholderMessage.isVisible = state is PlaylistsState.Empty

        if (state is PlaylistsState.Content) {
            binding.recyclerViewPlaylists.isVisible = true
            binding.recyclerViewPlaylists.adapter = playlistsAdapter
            playlistsAdapter.playlists.clear()
            playlistsAdapter.playlists.addAll(state.playlists)
            playlistsAdapter.notifyDataSetChanged()
        }
        else {
            binding.recyclerViewPlaylists.isVisible = false
        }
    }


    companion object {
        private const val PLACEHOLDER_TEXT = "placeholderText"

        fun newInstance(placeholderText: String) = MediaLibPlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLACEHOLDER_TEXT, placeholderText)
            }
        }
    }
}