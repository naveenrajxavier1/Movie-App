package com.troweprice.moviesData.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class GenreDeserializer : JsonDeserializer<GenreData> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GenreData {
        val jsonArray = json?.asJsonArray ?: throw JsonParseException("Expected a JsonArray")

        if (jsonArray.size() != 2) {
            throw JsonParseException("Genre array must contain exactly two elements: [Genre: String, No of movies count: Int]")
        }

        return GenreData(
            name = jsonArray[0].asString,
            noOfMovies = jsonArray[1].asInt
        )
    }
}