package com.practicum.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist

class PlaylistViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val playlistTracksCount: TextView = itemView.findViewById(R.id.playlistTracksCount)
    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImage)

    fun bind(model: Playlist) {

        Glide.with(itemView.context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_playlist_placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)))
            .into(playlistImage)

        playlistName.text = model.playlistName
        playlistTracksCount.text = model.countTracks.toString()
    }
}