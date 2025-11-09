package com.troweprice.moviesData.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesdomain.model.Genre
import javax.inject.Inject

class GenreDataToGenreMapper @Inject constructor() : IMapper<GenreData, Genre> {
    override fun map(input: GenreData) = Genre(name = input.name, movieCount = input.noOfMovies)
}