package com.example.playlistmaker.library.domain.model

sealed class QueryHandlingResult {
    object QuerySuccess : QueryHandlingResult()
    object QueryHandlingError : QueryHandlingResult()

}