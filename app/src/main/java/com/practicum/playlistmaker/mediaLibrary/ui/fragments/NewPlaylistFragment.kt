package com.practicum.playlistmaker.mediaLibrary.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist
import com.practicum.playlistmaker.mediaLibrary.ui.viewmodels.NewPlaylistCreationModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {

    lateinit var binding: FragmentNewPlaylistBinding
    val handler = android.os.Handler(Looper.getMainLooper())

    private val viewModel: NewPlaylistCreationModel by viewModel()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    var isPlaylistImageSelected = false
    var selectedImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextToSaveButtonAnToolbar()

        viewModel.observeState().observe(viewLifecycleOwner) {
            executeAction(it)
        }

        confirmDialog = createConfirmDialog()

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
    }

    fun executeAction(isButtonEnabled: Boolean) {
        binding.createNewPlaylistButton.isEnabled = isButtonEnabled
    }

    private fun createConfirmDialog(): MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.playlist_confim_dialog_title))
        .setMessage(getString(R.string.playlist_confim_dialog_message))
        .setNeutralButton(getString(R.string.playlist_confim_dialog_neutral_button_message)) { dialog, which -> }
        .setPositiveButton(getString(R.string.playlist_confim_dialog_positive_button_message)) { dialog, which ->
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    fun createTextChangeListener() {
        binding.newPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.createNewPlaylistButton.isEnabled = p0?.trim()?.isNotEmpty() ?: false
            }
        })
    }

    open fun returnBack() {
        if (binding.newPlaylistName.text?.isNotEmpty()!! ||
            binding.newPlaylistDescription.text?.isNotEmpty()!! ||
            isPlaylistImageSelected
        ) {
            confirmDialog.show().apply {
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.blue, null))
                getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(resources.getColor(R.color.blue, null))
            }
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun savePlaylistImageToPrivateStorage(uri: Uri): Uri {
        val filePath = requireContext().getDir(getString(R.string.playlists_storage_name), MODE_PRIVATE)
        if (!filePath.exists()) filePath.mkdirs()

        val playlistName = binding.newPlaylistName.text.toString()
        val file = filePath.resolve(playlistName)

        requireActivity().contentResolver.openInputStream(uri)!!.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }

        return file.toUri()
    }

    open fun createPlaylist(savedImageUri: Uri?) {
        val newPlaylist = Playlist(
            id = null,
            playlistName = binding.newPlaylistName.text.toString(),
            playlistDescription = binding.newPlaylistDescription.text.toString(),
            imageUrl = savedImageUri?.toString(),
            tracksIds = null,
            countTracks = 0
        )
        viewModel.createNewPlaylist(newPlaylist)
        val playlistCreatedMessage = getString(R.string.playlist_created, binding.newPlaylistName.text)
        Toast.makeText(requireContext(), playlistCreatedMessage, Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    open fun setTextToSaveButtonAnToolbar() {
        binding.toolbarNewPlaylistAndBackButton.title = getString(R.string.new_playlist)
        binding.createNewPlaylistButton.text = getString(R.string.create)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}