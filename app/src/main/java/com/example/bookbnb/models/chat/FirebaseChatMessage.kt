package com.example.bookbnb.models.chat

import com.example.bookbnb.utils.SessionManager
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

data class FirebaseChatMessage(
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US)
        if (timestamp is Long){
            date = Date(timestamp)
        }
        return "$senderName (${dateFormat.format(date)}):";
    }
}