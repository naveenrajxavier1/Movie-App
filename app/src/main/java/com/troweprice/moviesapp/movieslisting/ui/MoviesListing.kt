package com.troweprice.moviesapp.movieslisting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troweprice.moviesapp.R
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesapp.movieslisting.ui.model.MovieUi
import com.troweprice.moviesapp.ui.theme.yellow

@Composable
fun MoviesListingScreen(
    modifier: Modifier = Modifier,
    uiState: MovieListingUiState,
    isGenreDropDownExpanded: Boolean,
    genresUiState: GenresUiState,
    onDismissRequest: () -> Unit,
    onMovieClicked: (MovieUi) -> Unit,
    onPaginationReached: (genre: String) -> Unit,
    onSelectGenreClicked: () -> Unit,
    onGenreSelected: (GenreUi) -> Unit
) {

        when (uiState) {
            MovieListingUiState.Loading -> {
                Progress()
            }

            is MovieListingUiState.Error -> {
                ErrorScreen(error = uiState.message)
            }

            is MovieListingUiState.Success -> {
                SucessScreen(
                    modifier = modifier,
                    movies = uiState.list,
                    selectedGenre = uiState.selectedGenre,
                    showPaginationLoader = uiState.isPaginatedRequest,
                    isEndReached = uiState.isEndReached,
                    genresUiState = genresUiState,
                    isGenreDropDownExpanded = isGenreDropDownExpanded,
                    onDismissRequest = onDismissRequest,
                    onMovieClicked = onMovieClicked,
                    onPaginationReached = onPaginationReached,
                    onSelectGenreClicked = onSelectGenreClicked,
                    onGenreSelected = onGenreSelected
                )
            }
        }
}

@Composable
fun SucessScreen(
    modifier: Modifier = Modifier,
    movies: List<MovieUi>,
    selectedGenre: String,
    showPaginationLoader: Boolean,
    isEndReached: Boolean,
    genresUiState: GenresUiState,
    isGenreDropDownExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onMovieClicked: (MovieUi) -> Unit,
    onPaginationReached: (genre: String) -> Unit,
    onSelectGenreClicked: () -> Unit,
    onGenreSelected: (GenreUi) -> Unit
) {
    Column(modifier = modifier) {
        MovieListingHeader(
            showGenreSection = true,
            selectedGenre = selectedGenre,
            isGenreDropDownExpanded = isGenreDropDownExpanded,
            onSelectGenreClicked = onSelectGenreClicked,
            genresUiState = genresUiState,
            onDismissGenreRequest = onDismissRequest,
            onGenreSelected = onGenreSelected,
        )
        MoviesListing(
            list = movies,
            showPaginationLoader = showPaginationLoader,
            onMovieClicked = onMovieClicked,
            onPaginationReached = {
                onPaginationReached.invoke(selectedGenre)
            },
            isEndOfMovies = isEndReached
        )
    }
}


@Composable
fun ErrorScreen(error: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = error)
    }
}

@Composable
fun MovieListingHeader(
    showGenreSection: Boolean = true,
    selectedGenre: String,
    isGenreDropDownExpanded: Boolean = false,
    genresUiState: GenresUiState,
    onGenreSelected: (GenreUi) -> Unit,
    onSelectGenreClicked: () -> Unit,
    onDismissGenreRequest: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        if (showGenreSection) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_genre),
                    color = Color.Blue,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onSelectGenreClicked.invoke() }
                        .padding(16.dp)
                )
                Text(
                    text = selectedGenre,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )

                if (isGenreDropDownExpanded) {
                    GenreDropDown(
                        genresUiState = genresUiState,
                        onGenreSelected = onGenreSelected,
                        onDismissRequest = onDismissGenreRequest,
                        expanded = isGenreDropDownExpanded
                    )
                }
            }
        }
    }
}

@Composable
fun MoviesListing(
    list: List<MovieUi>,
    showPaginationLoader: Boolean,
    onMovieClicked: (MovieUi) -> Unit,
    isEndOfMovies: Boolean,
    onPaginationReached: () -> Unit
) {
    Box {
        val listState = rememberLazyListState()
        val isAtBottom by remember {
            derivedStateOf {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
            }
        }
        LaunchedEffect(isAtBottom, isEndOfMovies) {
            if (isAtBottom && !isEndOfMovies) {
                onPaginationReached()
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
            items(list, key = { movie -> movie.id }) {
                MovieTile(it, onMovieClicked)
            }
        }

        if (showPaginationLoader) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun Progress() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun MovieTile(movie: MovieUi, onMovieClicked: (MovieUi) -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .clickable { onMovieClicked(movie) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    ) {
        Column(Modifier.background(yellow)) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = movie.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(.85f, false)
                )
                Text(
                    text = "(${movie.releaseYear})",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(.20f)
                )
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            )
            Text(
                text = movie.genres,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
fun ShowMoviesListingScreen() {
    MoviesListingScreen(
        uiState = MovieListingUiState.Success(
            getMovieList(),
            isEndReached = false,
            isPaginatedRequest = true,
            selectedGenre = ALL_GENRE
        ), onMovieClicked = {},
        modifier = Modifier,
        onPaginationReached = {},
        onSelectGenreClicked = {},
        isGenreDropDownExpanded = false,
        onDismissRequest = {},
        genresUiState = GenresUiState.Success(getGenreList()),
        onGenreSelected = {}
    )
}


@Composable
@Preview(showBackground = true)
fun ShowMovieTile() {
    MovieTile(movie, {})
}

private fun getMovieList(): List<MovieUi> {
    return mutableListOf<MovieUi>().apply {
        repeat(10) {
            add(movie.copy(id = (it + 1).toString()))
        }
    }
}

private val movie = MovieUi(
    id = "1",
    genres = "Action, Comedy, Drama, Thriller, Mystery, Sci-Fi",
    releaseYear = "2022",
    title = "The Batman is a 2022 American superhero film based on the The Batman is a 2022 American superhero film based",
    overview = "The Batman is a superhero who appears in American comic books published by DC Comics. Batman was created by artist Bob Kane and writer Bill Finger, and debuted in the 27th issue of the comic book Detective Comics on March 30, 1939.",
    url = ""
)