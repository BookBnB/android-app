package com.example.bookbnb.network

import com.example.bookbnb.models.chat.FirebaseChat
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.models.chat.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FirebaseDBService {
    private var database: DatabaseReference = Firebase.database.reference
    private val USERS_KEY: String = "users"
    private val CHATS_KEY: String = "chats"
    private val MESSAGES_KEY: String = "messages"
    private val CHAT_ID_DELIMITER = "_"

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

    fun getChats(anfitrionUserId: String){

    }

    fun updateChat(huespedUserId: String, anfitrionUserId: String){
        database.child(USERS_KEY).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userHuesped =
                    dataSnapshot.child(huespedUserId).getValue(FirebaseUser::class.java)
                val userAnfitrion =
                    dataSnapshot.child(anfitrionUserId).getValue(FirebaseUser::class.java)
                val chatId = getChatId(huespedUserId, anfitrionUserId)
                val huespedChat = FirebaseChat(
                    chatId,
                    userAnfitrion?.name
                )
                val anfitrionChat = FirebaseChat(
                    chatId,
                    userHuesped?.name
                )
                database.child(CHATS_KEY).child(chatId).setValue(huespedChat)
                database.child(CHATS_KEY).child(chatId).setValue(anfitrionChat)
            }
        })
    }

    fun saveMessage(chatId: String, senderId: String, senderName: String, msg: String){
        val chatMsg = FirebaseChatMessage(chatId, senderId, senderName, msg)
        database.child(MESSAGES_KEY).child(chatId).push().setValue(chatMsg);
    }

    fun getChatId(huespedUserId: String, anfitrionUserId: String) : String{
        return "${huespedUserId}$CHAT_ID_DELIMITER$anfitrionUserId"
    }

    fun getUserIdsFromChatId(chatId: String) : List<String> {
        return chatId.split(CHAT_ID_DELIMITER)
    }
}