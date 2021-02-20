package com.example.bookbnb.models

data class User(var city: String?,
                   var email: String,
                   var id: String,
                   var name: String,
                   var phone: String?,
                   var role: String,
                   var surname: String) {
    fun getFullName() : String{
        return "$name $surname"
    }
}
