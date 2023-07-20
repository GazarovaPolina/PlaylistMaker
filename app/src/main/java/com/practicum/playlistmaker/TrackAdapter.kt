package com.practicum.playlistmaker

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(var tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    var onItemClick :((Track) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            //Toast.makeText(context, "message", Toast.LENGTH_SHORT).show()
           // Log.d("aaaaaaaa", "wwwwww")
            onItemClick?.invoke(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

