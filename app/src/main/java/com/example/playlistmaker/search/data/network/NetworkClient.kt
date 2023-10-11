package com.example.playlistmaker.search.data.network

interface NetworkClient {
    fun doRequest(dto: Any): Response
}