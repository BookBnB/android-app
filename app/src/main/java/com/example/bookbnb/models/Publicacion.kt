package com.example.bookbnb.models

class Publicacion(var id: Int,
                  var description: String,
                  var title: String,
                  var imageUrl: String,
                  var pricePerNight: Float,
                  var location: String,
                  var lat: String,
                  var long: String) {

    override fun toString(): String {
        return "$id|$description|$title|$imageUrl|$pricePerNight|$location|$lat|$long"
    }

}