package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(
            requireArguments().getString(
                ARGS_ID
            )
        )
    }

    private var currentPlaylist : Playlist? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            executeAction(it)
        }

        createTextChangeListener()

        binding.toolbarNewPlaylistAndBackButton.setOnClickListener {
            returnBack()
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.addImage.setImageURI(uri)
                binding.addImage.scaleType = ImageView.ScaleType.CENTER_CROP
                selectedImageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            isPlaylistImageSelected = true
        }

        binding.createNewPlaylistButton.setOnClickListener {
            val savedImageUri = selectedImageUri?.let { uri -> savePlaylistImageToPrivateStorage(uri) }
            createPlaylist(savedImageUri)
        }

        viewModel.getPlaylist()

        viewModel.playlistDetailsToEdit.observe(viewLifecycleOwner) {playlist ->
            currentPlaylist = playlist
            Glide.with(requireContext()).load(playlist.imageUrl).placeholder(R.drawable.ic_playlist_cover_placeholder).centerCrop()
                .into(binding.addImage)
            binding.newPlaylistName.setText(playlist.playlistName)
            binding.newPlaylistDescription.setText(playlist.playlistDescription)
        }
    }


    override fun returnBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun setTextToSaveButtonAnToolbar() {
        binding.toolbarNewPlaylistAndBackButton.title = getString(R.string.edit)
        binding.createNewPlaylistButton.text = getString(R.string.save)
    }

    override fun createPlaylist(savedImageUri: Uri?) {
        var uri = savedImageUri.toString()
        if (!isPlaylistImageSelected) {
            uri = currentPlaylist?.imageUrl.toString()
        }
        val editedPlaylist = Playlist(
            id = currentPlaylist?.id,
            playlistName = binding.newPlaylistName.text.toString(),
            playlistDescription = binding.newPlaylistDescription.text.toString(),
            imageUrl = uri,
            tracksIds = currentPlaylist?.tracksIds,
            countTracks = currentPlaylist!!.countTracks
        )
        viewModel.saveEditedPlaylist(editedPlaylist)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }


    companion object {
        private const val ARGS_ID = "id"

        fun createArgs(id: String): Bundle =
            bundleOf(
                ARGS_ID to id
            )
    }
}