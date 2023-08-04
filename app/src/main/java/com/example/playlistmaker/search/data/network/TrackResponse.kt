package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackDto
import java.util.ArrayList

class TrackResponse(
    val results: ArrayList<TrackDto>
) : Response()