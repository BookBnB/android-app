package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.models.chat.FirebaseChat
import com.example.bookbnb.models.chat.FirebaseChatMessage

class ChatsViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _chats = MutableLiveData<List<FirebaseChat>>(mutableListOf<FirebaseChat>())
    val chats: MutableLiveData<List<FirebaseChat>>
        get() = _chats
}

class ChatsViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatsViewModel::class.java)) {
            return ChatsViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}