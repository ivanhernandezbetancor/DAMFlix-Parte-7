package com.example.damflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.damflix.model.PeliculaRepository

class PeliculaViewModelFactory(
    private val repository: PeliculaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Validamos que se nos pida instanciar nuestro PeliculaViewModel
        if (modelClass.isAssignableFrom(PeliculaViewModel::class.java)) {
            return PeliculaViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel no reconocida por la fábrica: ${modelClass.name}")
    }
}