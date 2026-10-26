package org.ncg.fotoapp.models

data class Foto(
    val id: Long,
    val ruta: String,
    val comentario: String,
    val lat: Double,
    val lon: Double,
    val fecha: String
)
