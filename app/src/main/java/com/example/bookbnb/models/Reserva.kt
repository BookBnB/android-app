package com.example.bookbnb.models

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
        return estado == ESTADO_PENDIENTE && !isFinished()
    }

    fun calcularPrecioTotal() : Float {
        var format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var convertedDate = format.parse(fechaInicio)
        val diff = format.parse(fechaFin)!!.time - format.parse(fechaInicio)!!.time
        val nights = TimeUnit.MILLISECONDS.toDays(diff)
        return nights * precioPorNoche
    }

    fun isFinished() : Boolean{
        var format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var convertedDate = format.parse(fechaInicio)
        val endDateTimestamp = format.parse(fechaFin)!!.time
        return endDateTimestamp < Date().time
    }
}