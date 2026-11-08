package com.example.damflix.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "peliculas")
data class PeliculaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val director: String,
    val anio: Int,
    val esVista: Boolean = false,
    val esFavorita: Boolean = false
)
