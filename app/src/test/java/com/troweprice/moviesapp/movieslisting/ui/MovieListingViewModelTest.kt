package com.troweprice.moviesapp.movieslisting.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.troweprice.commonlib.IMapper
import com.troweprice.moviesapp.R
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesapp.movieslisting.ui.model.MovieUi
import com.troweprice.moviesapp.utils.MainDispatcherRule
import com.troweprice.moviesdomain.GenreResult
import com.troweprice.moviesdomain.GetGenres
import com.troweprice.moviesdomain.GetMovies
import com.troweprice.moviesdomain.MoviesResult
import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesdomain.model.Movie
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListingViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getMovies: GetMovies

    @MockK
    private lateinit var getGenres: GetGenres

    @MockK
    private lateinit var movieToMovieUiMapper: IMapper<Movie, MovieUi>

    @MockK
    private lateinit var genreUiListBuilder: GenreUiListBuilder

    private lateinit var viewModel: MovieListingViewModel

    @Before
    fun setUp() {
        viewModel = MovieListingViewModel(
            getMovies = getMovies,
            getGenres = getGenres,
            movieToMovieUiMapper = movieToMovieUiMapper,
            genreUiListBuilder = genreUiListBuilder
        )
    }

    @Test
    fun `handleIntent LoadPaginatedMovies Given GetMovies returns success Then updates state from Success-Loading to Success`() = runTest {
        // GIVEN
        val domainMovies = listOf(mockk<Movie>())
        val uiMovies = listOf(mockk<MovieUi>())
        val genre = "Action"
        val successResult = MoviesResult.Success(movies = domainMovies, isLastPage = false)

        coEvery { getMovies(isFreshLoading = false, genre = genre) } returns successResult
        coEvery { movieToMovieUiMapper.map(any()) } returns uiMovies.first()

        // VERIFY
        val stateJob = launch {
            viewModel.movieListingUiState.test {
                assertTrue("State should be Loading", awaitItem().isLoading )
                val successState = awaitItem()
                assertEquals(uiMovies, successState.list)
                expectNoEvents()
            }
        }
        // TEST
        viewModel.handleIntent(MoviesScreenIntent.LoadPaginatedMovies(genre = genre))
        stateJob.cancel()
    }

    @Test
    fun `handleIntent LoadMovies Given GetMovies returns success Then updates state from Loading to Success`() = runTest {
        // GIVEN
        val domainMovies = listOf(mockk<Movie>())
        val uiMovies = listOf(mockk<MovieUi>())
        val genre = "Action"
        val successResult = MoviesResult.Success(movies = domainMovies, isLastPage = false)

        coEvery { getMovies(isFreshLoading = true, genre = genre) } returns successResult
        coEvery { movieToMovieUiMapper.map(any()) } returns uiMovies.first()

        // VERIFY
        val stateJob = launch {
            viewModel.movieListingUiState.test {
                assertTrue("State should be Loading", awaitItem().isLoading)
                val successState = awaitItem()
                assertEquals(uiMovies, successState.list)
                expectNoEvents()
            }
        }
        // TEST
        viewModel.handleIntent(MoviesScreenIntent.LoadMovies(genre = genre))
        stateJob.cancel()
    }

    @Test
    fun `handleIntent LoadMovies Given GetMovies returns generic error Then updates state to Error and sends effect`() =
        runTest {
            // GIVEN
            val errorMessage = "Something went wrong"
            val genre = "Action"
            coEvery {
                getMovies(
                    isFreshLoading = true,
                    genre = genre
                )
            } returns MoviesResult.MoviesError.GenericError(errorMessage)

            // WHEN & THEN
            val stateJob = launch {
                viewModel.movieListingUiState.test {
                    assertTrue(
                        "State should be Loading",
                        awaitItem().isLoading
                    )
                    val errorState = awaitItem()
                    assertTrue("Expected Error state", errorState.error != null)
                    assertEquals(errorMessage, errorState.error?.errorMessage)
                    expectNoEvents()
                }
            }

            val effectJob = launch { // This `launch` is from the TestScope
                viewModel.effectFlow.test {
                    val expectedEffect =
                        MovieScreenEffect.ShowSnackBar(R.string.generic_error, errorMessage)
                    assertEquals(expectedEffect, awaitItem())
                    expectNoEvents()
                }
            }

            viewModel.handleIntent(MoviesScreenIntent.LoadMovies(genre = genre))

            stateJob.cancel()
            effectJob.cancel()
        }


    @Test
    fun `handleIntent LoadMovies Given GetMovies returns no internet Then updates state to Error and sends effect`() =
        runTest {
            // GIVEN
            val genre = "Action"
            coEvery { getMovies(isFreshLoading = true, genre = genre) } returns MoviesResult.MoviesError.NoInternetException

            // WHEN & THEN
            val stateJob = launch {
                viewModel.movieListingUiState.test {
                    assertTrue("State should be Loading", awaitItem().isLoading)
                    assertTrue("Expected Error state", awaitItem().error!= null)
                    expectNoEvents()
                }
            }

            val effectJob = launch {
                viewModel.effectFlow.test {
                    val expectedEffect =
                        MovieScreenEffect.ShowSnackBar(R.string.no_internet_message)
                    assertEquals(expectedEffect, awaitItem())
                    expectNoEvents()
                }
            }

            viewModel.handleIntent(MoviesScreenIntent.LoadMovies(genre = genre))

            stateJob.cancel()
            effectJob.cancel()
        }

    @Test
    fun `handleIntent ShowGenres Given GetGenres returns success Then updates state and sends effect`() = runTest {
        // GIVEN
        val domainGenres = listOf(mockk<Genre>())
        val uiGenres = listOf(mockk<GenreUi>())
        val successResult = GenreResult.Success(domainGenres)
        coEvery { getGenres() } returns successResult
        coEvery { genreUiListBuilder.build(any(), any()) } returns uiGenres

        // WHEN & THEN
        val stateJob = launch {
            viewModel.genresUiState.test {
                val successState = awaitItem()
                assertTrue("Expected Success state", successState is GenresUiState.Success)
                assertEquals(uiGenres, (successState as GenresUiState.Success).list)
                expectNoEvents()
            }
        }

        val effectJob = launch {
            viewModel.effectFlow.test {
                assertEquals(MovieScreenEffect.ShowGenreDropdown, awaitItem())
                expectNoEvents()
            }
        }

        viewModel.handleIntent(MoviesScreenIntent.ShowGenres)

        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun `handleIntent MovieClicked Then sends OpenMoviePage effect`() = runTest {
        // GIVEN
        val movieUrl = "https://example.com/movie/1"
        val movieUi = MovieUi("1", "Action", "2022", "The Batman", "An overview...", movieUrl)

        // WHEN & THEN
        viewModel.effectFlow.test {
            viewModel.handleIntent(MoviesScreenIntent.MovieClicked(movieUi))
            assertEquals(MovieScreenEffect.OpenMoviePage(movieUrl), awaitItem())
            expectNoEvents()
        }
    }
}
