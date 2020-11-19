package com.example.bookbnb.models

import com.squareup.moshi.JsonClass

class CustomImage(var url: String)
@JsonClass(generateAdapter = true)
class Publicacion(var id: String? = null,
                  var titulo: String,
                  var descripcion: String,
                  var precioPorNoche: Float,
                  var direccion: CustomLocation,
                  var cantidadDeHuespedes: Int,
                  var imagenes: List<CustomImage>
)