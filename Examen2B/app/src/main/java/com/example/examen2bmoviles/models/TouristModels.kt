package com.example.examen2bmoviles.models

data class TouristInfo(
    val idTurista: String = "turista_demo_001",
    val nombreRestaurante: String = "Restaurante El Quiteño",
    val latitudRestaurante: Double = -0.2201,
    val longitudRestaurante: Double = -78.5124,
    val horaReserva: String = "13:00"
)

data class QuitoActivity(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val costoBase: Double,
    val categoria: String,
    val guia: String
)

data class ActivitySelection(
    val touristInfo: TouristInfo,
    val actividad: QuitoActivity,
    val costoFinal: Double
)
