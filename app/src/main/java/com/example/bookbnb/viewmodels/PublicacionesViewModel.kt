package com.example.bookbnb.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookbnb.models.Publicacion

class PublicacionesViewModel : ViewModel()  {

    private val _publicaciones = MutableLiveData<List<Publicacion>>()

    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones

    init{
        val publicacion: Publicacion = Publicacion(1, "Test", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado")
        val publicacion2: Publicacion = Publicacion(1, "Test", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado")
        _publicaciones.value = listOf(publicacion, publicacion2)
    }
}