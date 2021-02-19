package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.utils.SessionManager
import com.google.firebase.ktx.Firebase

class ChatViewModel(application: Application, var huespedId: String, var anfitrionId: String) : BaseAndroidViewModel(application) {
    val chatId: String = FirebaseDBService().getChatId(huespedId, anfitrionId)

    private val _messageText = MutableLiveData<String>("")
    val messageText: MutableLiveData<String>
        get() = _messageText

    private val _messages = MutableLiveData<List<FirebaseChatMessage>>(mutableListOf<FirebaseChatMessage>())
    val messages: MutableLiveData<List<FirebaseChatMessage>>
        get() = _messages

    fun onSendClick(){
        val sessionMgr = SessionManager(getApplication())
        FirebaseDBService().saveMessage(chatId,
            sessionMgr.getUserId()!!,
            sessionMgr.getUserFullName()!!,
            messageText.value!!)
        _messageText.value = ""
    }
}

class ChatViewModelViewModelFactory(val app: Application, val huespedId: String, val anfitrionId: String) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(app, huespedId, anfitrionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}