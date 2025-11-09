package com.troweprice.moviesData.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a single movie entity, matching the structure of the movie data source.
 */
data class MovieData(

    @SerializedName("id")
    val id: String,

    @SerializedName("genres")
    val genres: List<String>?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("tagline")
    val tagline: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("url")
    val url: String?
)