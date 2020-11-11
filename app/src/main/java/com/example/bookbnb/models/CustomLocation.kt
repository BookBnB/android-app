package com.example.bookbnb.models

class Coordenada(var latitud: Double, var longitud: Double)

class CustomLocation(var pais: String, var provincia: String, var ciudad: String, var direccion: String, var coordenadas: Coordenada){
    override fun toString(): String {
        return "$direccion, $provincia, $ciudad, $pais"
    }
}