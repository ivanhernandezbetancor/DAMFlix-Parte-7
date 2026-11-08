package com.example.damflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.damflix.model.PeliculaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetallePeliculaViewModel(
    private val repository: PeliculaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargarDetallePelicula(peliculaId: Int, mensajeNoEncontrada: String) {
        viewModelScope.launch {
            _uiState.value = DetalleUiState.Cargando

            val resultado = withContext(Dispatchers.IO) {
                delay(1000)
                repository.obtenerPorId(peliculaId)
            }

            if (resultado != null) {
                _uiState.value = DetalleUiState.Exito(resultado)
            } else {
                _uiState.value = DetalleUiState.Error(mensajeNoEncontrada)
            }
        }
    }}