package com.example.damflix.model

import kotlinx.coroutines.flow.Flow

class PeliculaRepository(private val dao: PeliculaDao) {

    val todasPeliculas: Flow<List<PeliculaEntity>> = dao.obtenerTodas()

    suspend fun insertar(pelicula: PeliculaEntity) {
        dao.insertar(pelicula)
    }

    suspend fun actualizar(pelicula: PeliculaEntity) {
        dao.actualizar(pelicula)
    }
    suspend fun obtenerPorId(id: Int): PeliculaEntity? {
        return dao.obtenerPorId(id)
    }
}
