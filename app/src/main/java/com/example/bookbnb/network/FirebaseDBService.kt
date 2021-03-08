package com.example.bookbnb.network

import com.example.bookbnb.models.chat.ChatMessageNotification
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
    private val NOTIFICATIONS_KEY = "notifications"

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

    fun updateChat(huespedUserId: String, anfitrionUserId: String, onSuccess: () -> Unit){
        database.child(USERS_KEY).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userHuesped =
                    dataSnapshot.child(huespedUserId).getValue(FirebaseUser::class.java)
                val userAnfitrion =
                    dataSnapshot.child(anfitrionUserId).getValue(FirebaseUser::class.java)
                val chatId = getChatId(huespedUserId, anfitrionUserId)
                val chat = FirebaseChat(
                    chatId,
                    userHuesped?.name,
                    userAnfitrion?.name,
                    huespedUserId,
                    anfitrionUserId
                )
                database.child(CHATS_KEY).child(chatId).setValue(chat)
                onSuccess()
            }
        })
    }

    fun saveMessage(chatId: String, senderId: String, receiverId: String, senderName: String, msg: String){
        val chatMsg = FirebaseChatMessage(chatId, senderId, receiverId, senderName, msg)
        val notification = ChatMessageNotification(chatId, receiverId, senderName, msg)
        database.child(MESSAGES_KEY).child(chatId).push().setValue(chatMsg);
        database.child(NOTIFICATIONS_KEY).push().setValue(notification)
    }

    fun getChatId(huespedUserId: String, anfitrionUserId: String) : String{
        return "${huespedUserId}$CHAT_ID_DELIMITER$anfitrionUserId"
    }

    fun getUserIdsFromChatId(chatId: String) : List<String> {
        return chatId.split(CHAT_ID_DELIMITER)
    }
}