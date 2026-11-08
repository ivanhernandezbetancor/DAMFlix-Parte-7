package com.example.damflix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.damflix.R
import com.example.damflix.model.PeliculaEntity
import com.example.damflix.viewmodel.PeliculaViewModel

@Composable
fun ListaPeliculasScreen(
    viewModel: PeliculaViewModel,
    onPeliculaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Recolección reactiva del StateFlow alimentado por el Flow de Room
    val peliculas by viewModel.listaPeliculas.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = peliculas,
            key = { p -> p.id } // PrimaryKey autogenerada por Room
        ) { pelicula ->
            TarjetaPeliculaItem(
                pelicula = pelicula,
                onClick = { onPeliculaClick(pelicula.id) },
                onToggleFavorito = { viewModel.conmutarFavorito(pelicula) }
            )
        }
    }
}

@Composable
fun TarjetaPeliculaItem(
    pelicula: PeliculaEntity,
    onClick: () -> Unit,
    onToggleFavorito: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pelicula.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.director_anio, pelicula.director, pelicula.anio),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onToggleFavorito) {
                Icon(
                    imageVector = if (pelicula.esFavorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorito),
                    tint = if (pelicula.esFavorita) Color.Red else Color.Gray
                )
            }
        }
    }
}
