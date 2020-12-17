package com.example.bookbnb.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Reserva(var id: String,
              var publicacionId: String,
              var huespedId: String,
              var fechaInicio: String,
              var fechaFin: String,
              var estado: String,
              var precioPorNoche: Float,
              var nombreHuesped: String
)