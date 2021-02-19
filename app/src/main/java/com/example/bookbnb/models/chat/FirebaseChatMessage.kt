package com.example.bookbnb.models.chat

import com.google.firebase.database.ServerValue
import java.util.*
import kotlin.collections.HashMap

data class FirebaseChatMessage(
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    var timestamp: HashMap<String, Any> = HashMap()
) {
    init {
        timestamp["time"] = ServerValue.TIMESTAMP
    }

    fun getMsgTitle() : String{
        var date: Date = Date()
        val timestamp = timestamp["time"]
        if (timestamp is Long){
            date = Date(timestamp)
        }
        return "$senderName (${date})";
    }
}