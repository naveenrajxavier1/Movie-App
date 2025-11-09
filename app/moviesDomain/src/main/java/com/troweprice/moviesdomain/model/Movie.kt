package com.troweprice.moviesdomain.model

/**
 * Represents a single movie domain class.
 */
data class Movie(
    val id: String,
    val genres: List<String>,
    val releaseDate: String,
    val title: String,
    val overview: String,
    val url: String
)