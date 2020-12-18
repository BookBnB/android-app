package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModel(application: Application) : BaseAndroidViewModel(application) {

}

class ChatViewModelViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}