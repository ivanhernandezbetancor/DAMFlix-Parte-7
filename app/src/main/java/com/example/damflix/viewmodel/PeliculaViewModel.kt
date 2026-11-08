package com.example.damflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.damflix.model.PeliculaRepository
import com.example.damflix.model.PeliculaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class PeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {
    // Conversión automática del Flow de Room en StateFlow reactivo para Compose
    val listaPeliculas: StateFlow<List<PeliculaEntity>> = repository.todasPeliculas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun agregarPelicula(titulo: String, director: String, anio: Int, esVista: Boolean) {
        viewModelScope.launch {
            val nueva = PeliculaEntity(
                titulo = titulo,
                director = director,
                anio = anio,
                esVista = esVista
            )
            repository.insertar(nueva)
        }
    }
    fun conmutarFavorito(pelicula: PeliculaEntity) {
        viewModelScope.launch {
            val actualizada = pelicula.copy(esFavorita = !pelicula.esFavorita)
            repository.actualizar(actualizada)
        }
    }
}

