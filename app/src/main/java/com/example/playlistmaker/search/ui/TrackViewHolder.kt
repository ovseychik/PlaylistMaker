package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
    R.layout.view_track,parent,false)) {
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackDuration)
    private val artWorkView: ImageView = itemView.findViewById(R.id.artWork)

    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).toString()

        val round = itemView.context.resources.getDimensionPixelSize(R.dimen.album_cover_round_list)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(round))
            .placeholder(R.drawable.ic_album_cover_placeholder)
            .into(artWorkView)
    }

}

