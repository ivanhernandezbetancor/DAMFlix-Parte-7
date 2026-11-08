package com.example.damflix

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.damflix.viewmodel.DetallePeliculaViewModel
import com.example.damflix.viewmodel.DetalleUiState
import androidx.compose.runtime.getValue

// actividad 4: Página de información de la aplicación
// Pantalla de la ruta inicio -> muestra el perfil de usuario
@Composable
fun PantallaInicio() {
    PerfilUsuario()
}

// actividad 1
// Avatar redondo del usuario con borde
@Composable
fun CabeceraUsuario() {
    Image(
        painter = painterResource(id = R.drawable.user_avatar),
        contentDescription = stringResource(id = R.string.avatar_desc),
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}

// Nombre, rol y estadísticas del usuario
@Composable
fun InfoUsuario() {
    Column {
        Text(
            text = stringResource(id = R.string.user_name),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(id = R.string.user_rol),
            style = MaterialTheme.typography.bodyLarge
        )
        Row {
            Text(
                text = stringResource(id = R.string.estadisticas_peliculas, 42),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(id = R.string.estadisticas_resenas, 12),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Perfil completo: el avatar a la izquierda y la información a la derecha
@Composable
fun PerfilUsuario() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CabeceraUsuario()
        Spacer(modifier = Modifier.width(12.dp))
        InfoUsuario()
    }
}

// actividad 7
@Composable
fun PantallaDetalleScreen(
    peliculaId: Int,
    viewModel: DetallePeliculaViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mensajeNoEncontrada = stringResource(R.string.detalle_no_encontrada)

    LaunchedEffect(peliculaId) {
        viewModel.cargarDetallePelicula(peliculaId, mensajeNoEncontrada)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onVolver) {
            Text(text = stringResource(R.string.detalle_volver))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is DetalleUiState.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.detalle_cargando))
                    }
                }
            }
            is DetalleUiState.Exito -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = state.pelicula.titulo,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.detalle_director, state.pelicula.director),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.detalle_anio, state.pelicula.anio),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (state.pelicula.esVista) stringResource(R.string.detalle_vista)
                            else stringResource(R.string.detalle_pendiente),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (state.pelicula.esFavorita) stringResource(R.string.detalle_favorita)
                            else stringResource(R.string.detalle_no_favorita),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            is DetalleUiState.Error -> {
                Text(
                    text = state.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
