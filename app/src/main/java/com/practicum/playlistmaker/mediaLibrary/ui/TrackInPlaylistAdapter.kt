package com.practicum.playlistmaker.mediaLibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class TrackInPlaylistAdapter: RecyclerView.Adapter<TrackViewHolder>()  {

    var tracks = ArrayList<Track>()
    var onTrackItemClick: ((Track) -> Unit)? = null
    var onLongTrackItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        return TrackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onTrackItemClick?.invoke(tracks[position])
            this.notifyDataSetChanged()
        }

        holder.itemView.setOnLongClickListener {
            onLongTrackItemClick?.invoke(tracks[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}