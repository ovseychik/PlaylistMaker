package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_track,parent,false)) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val artWorkView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.trackName)
        artistNameView = itemView.findViewById(R.id.artistName)
        trackTimeView = itemView.findViewById(R.id.trackDuration)
        artWorkView = itemView.findViewById(R.id.artWork)
    }


    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).toString()

        val round = itemView.context.resources.getDimensionPixelSize(R.dimen.album_cover_round)

        Glide.with(itemView).load(track.artworkUrl100).centerCrop().transform(RoundedCorners(round))
            .placeholder(R.drawable.ic_album_cover_placeholder).into(artWorkView)
    }

}

