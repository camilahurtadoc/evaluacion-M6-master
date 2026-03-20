package com.app.zooapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla "animales" en la base de datos local.
 * Mapea los campos devueltos por la API, incluyendo los opcionales del detalle.
 */
@Entity(tableName = "animales")
data class AnimalEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val especie: String,
    val habitat: String,
    val dieta: String,
    val imagen: String,
    // Datos que solo vienen en la llamada de detalle (pueden ser null)
    val descripcion: String?,
    val estadoConservacion: String?,
    val esperanzaVida: String?,
    val pesoPromedio: String?,
    val alturaPromedio: String?,
    // Listas que se almacenan como JSON mediante TypeConverters
    val datosCuriosos: List<String>?,
    val comidasFavoritas: List<String>?,
    val predadoresNaturales: List<String>?
)
