package com.example.bookbnb.models.chat

data class ChatMessageNotification(val chatId: String = "",
                                   val receiverId: String = "",
                                   val senderName: String = "",
                                   val message: String = "")