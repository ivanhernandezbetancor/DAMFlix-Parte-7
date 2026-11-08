package com.example.damflix.viewmodel

import com.example.damflix.model.PeliculaEntity

sealed interface DetalleUiState {
    object Cargando : DetalleUiState
    data class Exito(val pelicula: PeliculaEntity) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}