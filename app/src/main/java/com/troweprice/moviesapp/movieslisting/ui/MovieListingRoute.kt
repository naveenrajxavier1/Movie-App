package com.troweprice.moviesapp.movieslisting.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MovieListingRoute(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: MovieListingViewModel = hiltViewModel<MovieListingViewModel>(),
    onMovieClick: (String) -> Unit
) {
    var genreDropDownExpanded by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<Pair<Int, String>?>(null) }

    LaunchedEffect(key1 = viewModel.effectFlow) {
        viewModel.effectFlow.collect {
            when (it) {
                MovieScreenEffect.ShowGenreDropdown -> genreDropDownExpanded = true
                is MovieScreenEffect.ShowSnackBar -> {
                    snackbarMessage = Pair(it.message, it.optionalMessage)
                }

                is MovieScreenEffect.OpenMoviePage -> onMovieClick.invoke(it.url)
            }
        }
    }
    snackbarMessage?.let {
        val message = stringResource(id = it.first)
        LaunchedEffect(message, snackbarHostState) {
            snackbarHostState.showSnackbar(message = " $message + ${it.second}")
            // Reset the state so the snackbar can be shown again with the same message
            snackbarMessage = null
        }
    }

    val movieListingUiState = viewModel.movieListingUiState.collectAsStateWithLifecycle().value
    val genreDropDownUiState = viewModel.genresUiState.collectAsStateWithLifecycle().value

    MoviesListingScreen(
        modifier = modifier,
        onMovieClicked = { viewModel.handleIntent(MoviesScreenIntent.MovieClicked(movie = it)) },
        uiState = movieListingUiState,
        onPaginationReached = { viewModel.handleIntent(MoviesScreenIntent.LoadPaginatedMovies(genre = it)) },
        onSelectGenreClicked = {
            viewModel.handleIntent(MoviesScreenIntent.ShowGenres)
        },
        isGenreDropDownExpanded = genreDropDownExpanded,
        onDismissRequest = { genreDropDownExpanded = false },
        genresUiState = genreDropDownUiState,
        onGenreSelected = {
            viewModel.handleIntent(MoviesScreenIntent.ChangeGenre(genreUi = it))
            genreDropDownExpanded = false
        })

}
