package com.example.playlistmaker.search.data.network

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}