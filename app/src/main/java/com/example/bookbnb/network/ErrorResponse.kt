package com.example.bookbnb.network

data class ErrorResponse(
    val name: String?,
    val message: String
    /*val error_description: String, // this is the translated error shown to the user directly from the API
    val causes: Map<String, String> = emptyMap() //this is for errors on specific field on a form*/
)