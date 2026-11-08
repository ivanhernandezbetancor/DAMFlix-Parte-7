package com.example.damflix.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.damflix.R

sealed class Ruta(val path: String, val titulo: Int, val icono: ImageVector) {
    object Inicio : Ruta("inicio", R.string.menu_inicio, Icons.Filled.Home)
    object Catalogo : Ruta("catalogo", R.string.menu_catalogo, Icons.Filled.List)
    object Formulario : Ruta("alta", R.string.menu_anadir_pelicula, Icons.Filled.Add)

    // Ruta dinámica con parámetro de ID (en minúsculas)
    object Detalle : Ruta("detalle/{peliculaId}", R.string.menu_detalle, Icons.Filled.Details) {
        fun crearRuta(peliculaId: Int) = "detalle/$peliculaId"
    }
}