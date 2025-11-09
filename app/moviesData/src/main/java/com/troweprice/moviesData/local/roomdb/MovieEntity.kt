package com.troweprice.moviesData.local.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** * Represents a single movie entity for the Room database.
 * The table is named "movies".
 */
@Entity(tableName = "movies")
data class MovieEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    // Room cannot store lists directly, so this will be handled by a TypeConverter.
    @ColumnInfo(name = "genres")
    val genres: List<String>,

    @ColumnInfo(name = "release_date")
    val releaseDate: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "tagline")
    val tagline: String,

    @ColumnInfo(name = "overview")
    val overview: String,

    @ColumnInfo(name = "url")
    val url: String
)