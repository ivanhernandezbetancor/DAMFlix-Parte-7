package com.example.damflix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.damflix.R
import com.example.damflix.viewmodel.PeliculaViewModel

@Composable
fun FormularioAgregarPeliculaScreen(
    viewModel: PeliculaViewModel,
    onPeliculaGuardada: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var titulo by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var anioTexto by remember { mutableStateOf("") }
    var esVista by remember { mutableStateOf(false) }

    val esFormularioValido = titulo.isNotBlank() &&
            director.isNotBlank() &&
            anioTexto.toIntOrNull() != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.añadir_pelicula_room),
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.titulo_pelicula)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = director,
            onValueChange = { director = it },
            label = { Text(stringResource(R.string.director_pelicula)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = anioTexto,
            onValueChange = { anioTexto = it },
            label = { Text(stringResource(R.string.anio_estreno)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = esVista, onCheckedChange = { esVista = it })
            Text(text = stringResource(R.string.pelicula_vista))
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val anio = anioTexto.toIntOrNull() ?: 0
                viewModel.agregarPelicula(
                    titulo = titulo,
                    director = director,
                    anio = anio,
                    esVista = esVista
                )
                titulo = ""
                director = ""
                anioTexto = ""
                esVista = false
                onPeliculaGuardada()
            },
            enabled = esFormularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.guardar_pelicula))
        }
    }
}
