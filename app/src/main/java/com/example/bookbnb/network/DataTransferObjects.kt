package com.example.bookbnb.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    var email: String,
    var password: String,
    var name: String,
    var apellido: String,
    var telefono: String?,
    var ciudad: String?,
    var role: String)

data class RegisterResponse(
    var email: String,
    var password: String,
    val role: String
)

@JsonClass(generateAdapter = true)
data class LoginDTO(
    var email: String,
    var password: String)

data class LoginResponse(
    val token: String
)