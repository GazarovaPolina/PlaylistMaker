package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.NewPlaylistCreationModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistCreationModel by viewModel()

    private lateinit var confimDialog: MaterialAlertDialogBuilder
    private var isPlaylistImageSelected = false
    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            executeAction(it)
        }

        confimDialog = createConfimDialog()

        createTextChangeListener()

        binding.toolbarNewPlaylistAndBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnBack()
            }
        })

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.addImage.setImageURI(uri)
                savePlaylistImageToPrivateStorage(uri)
                imageUri = uri

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            isPlaylistImageSelected = true
        }

        binding.createNewPlaylistButton.setOnClickListener {
            createPlaylist()
        }
    }

    private fun executeAction(isButtonEnabled: Boolean) {
        binding.createNewPlaylistButton.isEnabled = isButtonEnabled
    }

    private fun createConfimDialog(): MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.playlist_confim_dialog_title))
        .setMessage(getString(R.string.playlist_confim_dialog_message))
        .setNeutralButton(getString(R.string.playlist_confim_dialog_neutral_button_message)) { dialog, which -> }
        .setPositiveButton(getString(R.string.playlist_confim_dialog_positive_button_message)) { dialog, which ->
            findNavController().navigateUp()
        }

    private fun createTextChangeListener() {
        binding.newPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.createNewPlaylistButton.isEnabled = p0?.isNotEmpty()!!
//                if (p0.isNotEmpty()) {
//                    binding.textInputLayoutNewPlaylistName.boxStrokeColor = context?.getColor(R.color.dark_blue)!!
//                }
//                else {
//                    binding.textInputLayoutNewPlaylistName.boxStrokeColor = context?.getColor(R.color.dark_grey)!!
//                }

            }
        })
    }

    private fun returnBack() {
        if (binding.newPlaylistName.text?.isNotEmpty()!! ||
            binding.newPlaylistDescription.text?.isNotEmpty()!! ||
            isPlaylistImageSelected
        ) {
            confimDialog.show().apply {
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.blue, null))
                getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(resources.getColor(R.color.blue, null))
            }
        } else {
            findNavController().navigateUp()
        }
    }

    private fun savePlaylistImageToPrivateStorage(uri: Uri) {
        val playlistName = binding.newPlaylistName.text.toString()
        val filePath = File(requireContext().getDir(playlistName, MODE_PRIVATE), getString(R.string.playlists_storage_name))
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, getString(R.string.playlist_image, playlistName))
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun createPlaylist() {
        val newPlaylist = Playlist(
            id = null,
            playlistName = binding.newPlaylistName.text.toString(),
            playlistDescription = binding.newPlaylistDescription.text.toString(),
            imageUrl = imageUri.toString(),
            tracksIds = null,
            countTracks = 0
        )
        viewModel.createNewPlaylist(newPlaylist)
        val playlistCreatedMessage = getString(R.string.playlist_created, binding.newPlaylistName.text)
        Toast.makeText(requireContext(), playlistCreatedMessage, Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}