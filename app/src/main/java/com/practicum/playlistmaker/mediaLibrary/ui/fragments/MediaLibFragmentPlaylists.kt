package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMediaLibPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.MediaLibPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibFragmentPlaylists : Fragment() {

    private lateinit var binding: FragmentMediaLibPlaylistsBinding

    private val viewModel: MediaLibPlaylistsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaLibPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaLibPlaylistsPlaceholderMessage.text = requireArguments().getString(MediaLibFragmentPlaylists.PLACEHOLDER_TEXT).toString()
    }

    companion object {
        private const val PLACEHOLDER_TEXT = "placeholderText"

        fun newInstance(placeholderText: String) = MediaLibFragmentPlaylists().apply {
            arguments = Bundle().apply {
                putString(PLACEHOLDER_TEXT, placeholderText)
            }
        }
    }
}