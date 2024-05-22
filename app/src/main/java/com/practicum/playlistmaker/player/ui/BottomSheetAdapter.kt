package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.Playlist

class BottomSheetAdapter: RecyclerView.Adapter<BottomSheetViewHolder>() {

    var playlists = ArrayList<Playlist>()
    var onItemClick:((Playlist)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetViewHolder(PlaylistViewBottomSheetBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
            this.notifyDataSetChanged()
        }
    }
}