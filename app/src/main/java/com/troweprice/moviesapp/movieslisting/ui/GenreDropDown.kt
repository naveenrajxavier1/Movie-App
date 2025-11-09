package com.troweprice.moviesapp.movieslisting.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesapp.ui.theme.yellow

@Composable
fun GenreDropDown(
    genresUiState: GenresUiState,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onGenreSelected: (GenreUi) -> Unit,
) {
    when (genresUiState) {
        is GenresUiState.Success -> {
            GenreListing(
                genres = genresUiState.list,
                onDismissRequest = onDismissRequest,
                expanded = expanded,
                onGenreSelected = onGenreSelected
            )
        }

        is GenresUiState.Error -> {
            Text(text = genresUiState.message)
        }

        GenresUiState.Loading -> {
            Text(text = "Loading..")
        }
    }
}

@Composable
fun GenreListing(
    genres: List<GenreUi>,
    onGenreSelected: (GenreUi) -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        genres.forEach { item ->
            DropdownMenuItem(enabled = !item.isSelected,
                text = { GenreTile(item) },
                onClick = {
                    onGenreSelected.invoke(item)
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun GenreTile(genre: GenreUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (genre.isSelected) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Default.Check,
                contentDescription = "Selected Genre"
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
        Text(
            text = genre.displayText,
            style = MaterialTheme.typography.bodyMedium,
            color = if (genre.isSelected) yellow else Color.Unspecified,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ShowGenreTile() {
    GenreTile(genre = GenreUi("Action", "Action (120 movies)", isSelected = false))
}

@Composable
@Preview(showBackground = true)
fun ShowGenes() {
    GenreListing(
        genres = getGenreList(),
        onDismissRequest = {},
        expanded = true,
        onGenreSelected = {})
}


fun getGenreList(): List<GenreUi> {
    return mutableListOf<GenreUi>().apply {
        repeat(10) {
            add(genre.copy(name = "Action (120 movies) ${(it + 1)}"))
        }

        set(5, genre.copy(name = "Action (120 movies)", isSelected = true))
    }
}

private val genre = GenreUi("Action", "Action (120 movies)", isSelected = false)