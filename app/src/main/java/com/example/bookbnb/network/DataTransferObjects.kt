package com.example.bookbnb.network

import android.os.Parcel
import android.os.Parcelable
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Publicacion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class CrearPublicacionResponse(
    var id: String
)

@JsonClass(generateAdapter = true)
data class LocationDTO(var consulta: String, var limite: Int)

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    var email: String,
    var password: String,
    var name: String,
    var surname: String,
    var phone: String?,
    var city: String?,
    var role: String)

data class RegisterResponse(
    var email: String,
    val role: String
)

@JsonClass(generateAdapter = true)
data class LoginDTO(
    var email: String,
    var password: String)

data class LoginResponse(
    val token: String
)