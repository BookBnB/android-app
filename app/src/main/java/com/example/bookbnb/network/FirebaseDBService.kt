package com.example.bookbnb.network

import com.example.bookbnb.models.FirebaseUser
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FirebaseDBService {
    private var database: DatabaseReference = Firebase.database.reference
    private val USERS_KEY: String = "users"

    fun createUserIfNotExists(userId: String, name: String, email: String?,
                             onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseUser(name, email)
        database.child(USERS_KEY).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.child(userId).exists()) {
                    database.child(USERS_KEY).child(userId).setValue(user)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure(it)
                        }
                }
            }
        })
    }
}