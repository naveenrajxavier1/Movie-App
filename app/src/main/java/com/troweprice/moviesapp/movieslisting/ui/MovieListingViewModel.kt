package com.troweprice.moviesapp.movieslisting.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troweprice.commonlib.IMapper
import com.troweprice.moviesapp.R
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesapp.movieslisting.ui.model.MovieUi
import com.troweprice.moviesdomain.GenreResult
import com.troweprice.moviesdomain.GetGenres
import com.troweprice.moviesdomain.GetMovies
import com.troweprice.moviesdomain.MoviesResult
import com.troweprice.moviesdomain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ALL_GENRE = "All"

@HiltViewModel
class MovieListingViewModel @Inject constructor(
    private val getMovies: GetMovies,
    private val getGenres: GetGenres,
    private val movieToMovieUiMapper: IMapper<Movie, MovieUi>,
    private val genreUiListBuilder: GenreUiListBuilder
) : ViewModel() {
    private val _movieListingUiState: MutableStateFlow<MovieListingUiState> =
        MutableStateFlow(MovieListingUiState.Loading)
    val movieListingUiState: StateFlow<MovieListingUiState> =
        _movieListingUiState.onStart { handleIntent(MoviesScreenIntent.LoadMovies(ALL_GENRE)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = MovieListingUiState.Loading
            )

    private val _genresUiState: MutableStateFlow<GenresUiState> =
        MutableStateFlow(GenresUiState.Loading)
    val genresUiState: StateFlow<GenresUiState> = _genresUiState

    private val _effectChannel = Channel<MovieScreenEffect>()
    val effectFlow: Flow<MovieScreenEffect> = _effectChannel.receiveAsFlow()

    fun handleIntent(intent: MoviesScreenIntent) {
        when (intent) {
            is MoviesScreenIntent.LoadMovies -> loadMovies(
                genreName = intent.genre
            ).also {
                println("loadMovies() w ith genreName: $intent.genre")
            }

            is MoviesScreenIntent.LoadPaginatedMovies -> loadMovies(
                genreName = intent.genre,
                isPaginatedRequest = true
            )

            is MoviesScreenIntent.ShowGenres -> showGenres()
            is MoviesScreenIntent.ChangeGenre -> showMovies(genreUi = intent.genreUi)
            is MoviesScreenIntent.MovieClicked -> openMoviePage(intent.movie)

        }
    }

    private fun openMoviePage(movie: MovieUi) {
        viewModelScope.launch {
            _effectChannel.send(MovieScreenEffect.OpenMoviePage(movie.url))
        }
    }

    private fun showMovies(genreUi: GenreUi) {
        loadMovies(genreUi.name)
    }

    private fun loadMovies(genreName: String, isPaginatedRequest: Boolean = false) {
        viewModelScope.launch {
            println("loadMovies called with genreName: $genreName isPaginated $isPaginatedRequest")
            if (!isPaginatedRequest) {
                _movieListingUiState.update { MovieListingUiState.Loading }
            } else {
                _movieListingUiState.value.takeIf { it is MovieListingUiState.Success }?.let {
                    _movieListingUiState.update {
                        (it as MovieListingUiState.Success).copy(isPaginatedRequest = true)
                    }
                }
            }

            when (val result = getMovies(
                isFreshLoading = !isPaginatedRequest,
                genre = genreName.takeIf { it != ALL_GENRE })) { // ALL genre will pass null genre
                is MoviesResult.Success -> {
                    _movieListingUiState.update {
                        MovieListingUiState.Success(list = result.movies.let {
                            it.map { movie -> movieToMovieUiMapper.map(movie) }
                        }, selectedGenre = genreName, result.isLastPage)
                    }
                }

                is MoviesResult.MoviesError.GenericError -> {
                    _movieListingUiState.update {
                        MovieListingUiState.Error(result.error)
                    }
                    _effectChannel.send(
                        MovieScreenEffect.ShowSnackBar(
                            R.string.generic_error,
                            result.error
                        )
                    )
                }

                MoviesResult.MoviesError.NoInternetException -> {
                    _movieListingUiState.update {
                        MovieListingUiState.Error("No internet connection")
                    }
                    _effectChannel.send(MovieScreenEffect.ShowSnackBar(R.string.no_internet_message))
                }
            }
        }
    }

    private fun showGenres() {
        viewModelScope.launch {
            when (val result = getGenres()) {
                is GenreResult.Success -> {
                    val selectedGenreName =
                        (_movieListingUiState.value as? MovieListingUiState.Success)?.selectedGenre

                    val fullGenreList = genreUiListBuilder.build(
                        domainGenres = result.genres,
                        selectedGenreName = selectedGenreName
                    )

                    _genresUiState.update { GenresUiState.Success(fullGenreList) }
                    _effectChannel.send(MovieScreenEffect.ShowGenreDropdown)
                }

                is GenreResult.GenreError.GenericError -> {
                    _effectChannel.send(
                        MovieScreenEffect.ShowSnackBar(
                            message = R.string.generic_error,
                            optionalMessage = result.error
                        )
                    )
                }

                is GenreResult.GenreError.NoInternetException -> {
                    _effectChannel.send(MovieScreenEffect.ShowSnackBar(R.string.generic_error))
                }
            }
        }
    }
}


sealed interface MoviesScreenIntent {
    data class LoadPaginatedMovies(val genre: String) : MoviesScreenIntent
    data class LoadMovies(val genre: String) :
        MoviesScreenIntent

    data class MovieClicked(val movie: MovieUi) : MoviesScreenIntent
    data object ShowGenres : MoviesScreenIntent
    data class ChangeGenre(val genreUi: GenreUi) : MoviesScreenIntent
}

sealed class MovieListingUiState {
    data object Loading : MovieListingUiState()
    data class Success(
        val list: List<MovieUi>,
        val selectedGenre: String,
        val isEndReached: Boolean = false,
        val isPaginatedRequest: Boolean = false
    ) :
        MovieListingUiState()

    data class Error(val message: String) : MovieListingUiState()
}

sealed interface GenresUiState {
    data object Loading : GenresUiState
    data class Success(val list: List<GenreUi>) : GenresUiState
    data class Error(val message: String) : GenresUiState
}

sealed interface MovieScreenEffect {
    data object ShowGenreDropdown : MovieScreenEffect
    data class OpenMoviePage(val url: String) : MovieScreenEffect
    data class ShowSnackBar(@StringRes val message: Int, val optionalMessage: String = "") :
        MovieScreenEffect
}