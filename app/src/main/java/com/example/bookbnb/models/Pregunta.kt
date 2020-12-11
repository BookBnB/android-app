package com.example.bookbnb.models

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@JsonClass(generateAdapter = true)
class Respuesta (
    var creada: Date,
    var descripcion: String,
    var usuarioId: String
)

@JsonClass(generateAdapter = true)
class Pregunta (
    var id: String?,
    var creada: Date?,
    var descripcion: String,
    var usuarioId: String?,
    var respuesta: Respuesta?
){
    fun getCreationDateAsShortString() : String{
        if (creada != null) {
            return SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(creada)
        }
        return ""
    }
}
