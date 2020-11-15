package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.models.Publicacion

class DetallePublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicacion = MutableLiveData<Publicacion>()

    val publicacion : LiveData<Publicacion>
        get() = _publicacion

    fun setPublicacion(publicacion: Publicacion) {
        _publicacion.value = publicacion
    }

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