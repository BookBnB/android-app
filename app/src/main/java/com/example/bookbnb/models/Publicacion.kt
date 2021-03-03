package com.example.bookbnb.models

import com.example.bookbnb.viewmodels.round
import com.squareup.moshi.JsonClass

class TipoDeAlojamientoProvider{
    companion object{
        val tipos : List<String> = listOf(
            "Alojamiento entero",
            "Habitación privada",
            "Habitación compartida",
            "Habitación de hotel"
        )
    }

}

@JsonClass(generateAdapter = true)
class Anfitrion(val id: String)

class CustomImage(var url: String)
@JsonClass(generateAdapter = true)
class Publicacion(var id: String? = null,
                  var titulo: String,
                  var descripcion: String,
                  var precioPorNoche: Float,
                  var direccion: CustomLocation,
                  var cantidadDeHuespedes: Int,
                  var imagenes: List<CustomImage>,
                  var tipoDeAlojamiento: String,
                  var anfitrion: Anfitrion? = null,
                  val estado: String? = null,
                  var calificaciones: List<Calificacion>? = null,
                  var calificacion: Float? = null
){
    fun calificacionAsString() : String {
        val currCalificacionAsString = if (calificacion == null)
            "-"
        else
             calificacion?.round(2)?.toFloat().toString()
        val cantCalificaciones = if (calificaciones.isNullOrEmpty()) 0 else calificaciones?.size
        return "$currCalificacionAsString (${cantCalificaciones})"
    }
}