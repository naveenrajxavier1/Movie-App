package com.troweprice.moviesData.local.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single genre entity for the Room database, including the movie count.
 * The table is named "genres".
 */
@Entity(tableName = "genres")
data class GenreEntity(

    /**
     * The unique name of the genre, which acts as its primary key.
     */
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    /**
     * The number of movies associated with this genre.
     */
    @ColumnInfo(name = "movie_count")
    val noOfMovies: Int
)