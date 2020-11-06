package com.example.bookbnb.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginDTO(
    var email: String,
    var password: String)


data class LoginResponse(
    val token: String
)