package com.example.bookbnb.models

class Coordenada(var latitud: Double, var longitud: Double)

class CustomLocation(var pais: String, var provincia: String, var municipio: String?, var ciudad: String?, var direccion: String?, var coordenadas: Coordenada){
    override fun toString(): String {
        var location = "";
        if (direccion != null) {
            location += "$direccion, "
        }
        if (municipio != null) {
            location += "$municipio, "
        }
        if (ciudad != null) {
            location += "$ciudad, "
        }
        location += "$provincia, $pais"
        return location
    }
}