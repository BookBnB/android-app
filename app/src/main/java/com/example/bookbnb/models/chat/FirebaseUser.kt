package com.example.bookbnb.models.chat

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseUser(
    var name: String? = "",
    var email: String? = ""
)