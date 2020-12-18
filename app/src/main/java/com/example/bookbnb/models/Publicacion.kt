package com.example.bookbnb.models

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
                  var anfitrion: Anfitrion? = null
)