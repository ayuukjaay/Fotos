package org.ncg.fotoapp.models

data class Usuario(
    val id: Long,
    val nombre: String,
    val apellidos: String,
    val correo: String
)