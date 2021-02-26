package com.example.bookbnb.viewmodels

import java.text.SimpleDateFormat
import java.util.*

data class CalificacionVM(var puntos: Int, var detalle: String, var huesped: String){
    fun puntosAsString(): String{
        return puntos.toString()
    }
}