package com.troweprice.moviesData.local.roomdb

import androidx.room.TypeConverter

class Converters {

    private val separator = ","

    @TypeConverter
    fun fromStringList(genres: List<String>): String {
        return genres.joinToString(separator)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return data.split(separator)
    }

}