package com.practicum.playlistmaker.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track


class TrackAdapter(var tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    var onItemClick :((Track) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

