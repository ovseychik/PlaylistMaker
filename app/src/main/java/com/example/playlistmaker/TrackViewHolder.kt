package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        trackTimeView.text = track.trackTime

        val round =itemView.context.resources.getDimensionPixelSize(R.dimen.album_cover_round)

        Glide.with(itemView).load(track.artWorkUrl).centerCrop().transform(RoundedCorners(round))
            .placeholder(R.drawable.ic_album_cover_placeholder).into(artWorkView)
    }

}

