package com.practicum.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist

class BottomSheetViewHolder(private val binding: PlaylistViewBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        Glide.with(itemView.context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_playlist_placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)))
            .into(binding.bottomSheetPlaylistImage)

        binding.bottomSheetPlaylistName.text = model.playlistName
        binding.bottomSheetCountPlaylistTracks.text =  TracksCountMessageEndingChanger().getTracksCountMessageEnding(model.countTracks)
    }
}