package com.example.bookbnb.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Reserva(var id: String? = null,
                  var titulo: String,
                  var autor: String,
                  var fechaInicio: String,
                  var fechaFin: String
)