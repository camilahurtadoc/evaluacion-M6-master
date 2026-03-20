package com.app.zooapp.data.api.model

import com.google.gson.annotations.SerializedName

data class AnimalDetailApi(
    val id: Int,
    val nombre: String,
    val especie: String,
    val habitat: String,
    val dieta: String,
    val imagen: String,
    val descripcion: String,
    @SerializedName("estadoDeConservacion") val estadoConservacion: String,
    @SerializedName("esperanzaDeVida") val esperanzaVida: String,
    @SerializedName("pesoPromedio") val pesoPromedio: String,
    @SerializedName("alturaPromedio") val alturaPromedio: String,
    @SerializedName("datosCuriosos") val datosCuriosos: List<String>,
    @SerializedName("comidasFavoritas") val comidasFavoritas: List<String>,
    @SerializedName("predadoresNaturales") val predadoresNaturales: List<String>,
)
