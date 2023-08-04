package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.network.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}