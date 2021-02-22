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
              var nombreHuesped: String?
) {
    companion object{
        val ESTADO_ACEPTADA = "aceptada"
        val ESTADO_PENDIENTE = "creada"
        val ESTADO_RECHAZADA = "rechazada"
    }

    fun isPendiente() : Boolean{
        return estado == ESTADO_PENDIENTE
    }
}