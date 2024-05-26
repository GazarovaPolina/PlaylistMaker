package com.practicum.playlistmaker.mediaLibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist

class PlaylistAdapter() : RecyclerView.Adapter<PlaylistViewHolder>() {

    val playlists = ArrayList<Playlist>()

    var onPlaylistItemClick:((Playlist)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistItemClick?.invoke(playlists[position])
            this.notifyDataSetChanged()
        }
    }
}