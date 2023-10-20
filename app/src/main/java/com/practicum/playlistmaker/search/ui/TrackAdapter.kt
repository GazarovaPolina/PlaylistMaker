package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder


class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    var onItemClick :((Track) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        return TrackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tracks[position])
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}

