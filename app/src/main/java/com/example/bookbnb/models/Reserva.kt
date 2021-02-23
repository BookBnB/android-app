package com.example.bookbnb.models

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

enum class EstadoReserva(val estado: String) {
    ACEPTADA("aceptada"),
    PENDIENTE("creada"),
    RECHAZADA("rechazada"),
    CANCELADA("cancelada")
}

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
        val ESTADO_CANCELADA = "cancelada"

        fun sortReservas(reservas: List<Reserva>) : List<Reserva>{
            val pastReservas = mutableListOf<Reserva>()
            val nextReservas = mutableListOf<Reserva>()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            reservas.map {
                if (it.isFinished()){
                    pastReservas.add(it)
                }
                else{
                    nextReservas.add(it)
                }
            }
            pastReservas.sortByDescending { format.parse(it.fechaFin)!!.time }
            nextReservas.sortBy { format.parse(it.fechaInicio)!!.time }
            return nextReservas + pastReservas
        }
    }

    fun convertEstado() : String{
        return when (estado){
            EstadoReserva.PENDIENTE.estado -> if (isFinished()) "Expirada" else "Pendiente"
            EstadoReserva.ACEPTADA.estado -> if (isFinished()) "Finalizada" else "Aceptada"
            EstadoReserva.RECHAZADA.estado -> "Rechazada"
            EstadoReserva.CANCELADA.estado -> "Cancelada"
            else -> "N/A"
        }
    }

    fun isPendiente() : Boolean{
        return estado == ESTADO_PENDIENTE && !isFinished()
    }

    fun calcularPrecioTotal() : Float {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var convertedDate = format.parse(fechaInicio)
        val diff = format.parse(fechaFin)!!.time - format.parse(fechaInicio)!!.time
        val nights = TimeUnit.MILLISECONDS.toDays(diff)
        return nights * precioPorNoche
    }

    fun isFinished() : Boolean{
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var convertedDate = format.parse(fechaInicio)
        val endDateTimestamp = format.parse(fechaFin)!!.time
        return endDateTimestamp < Date().time
    }

    fun isGradable() : Boolean{
        return isFinished() && estado == EstadoReserva.ACEPTADA.estado
    }
}