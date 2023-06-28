package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import java.util.ArrayList

class TrackResponse(
    //TODO: migrate to TrackDto
    val results: ArrayList<Track>
) : Response()