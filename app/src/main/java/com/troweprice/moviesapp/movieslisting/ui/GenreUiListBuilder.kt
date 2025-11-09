package com.troweprice.moviesapp.movieslisting.ui

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesdomain.model.Genre
import javax.inject.Inject

class GenreUiListBuilder @Inject constructor(
    private val genreToGenreUiMapper: IMapper<Genre, GenreUi>
) {
    fun build(
        domainGenres: List<Genre>,
        selectedGenreName: String?
    ): List<GenreUi> {
        val totalMoviesCount = domainGenres.sumOf { it.movieCount }

        // 1. Create the "All" genre UI model
        val allGenreUi = createAllGenreUi(totalMoviesCount, selectedGenreName)

        // 2. Map the domain genres to UI models and set their selection state
        val genresUi = domainGenres.map { genre ->
            val uiModel = genreToGenreUiMapper.map(genre)
            uiModel.copy(isSelected = uiModel.name == selectedGenreName)
        }

        // 3. Combine them and return the final list
        return listOf(allGenreUi) + genresUi
    }

    private fun createAllGenreUi(totalMoviesCount: Int, selectedGenreName: String?): GenreUi {
        val isSelected = selectedGenreName == null || selectedGenreName == ALL_GENRE
        return GenreUi(
            name = ALL_GENRE,
            displayText = "$ALL_GENRE ($totalMoviesCount)",
            isSelected = isSelected
        )
    }
}