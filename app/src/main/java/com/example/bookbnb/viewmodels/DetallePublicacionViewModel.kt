package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetallePublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {
}

class DetallePublicacionViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetallePublicacionViewModel::class.java)) {
            return DetallePublicacionViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}