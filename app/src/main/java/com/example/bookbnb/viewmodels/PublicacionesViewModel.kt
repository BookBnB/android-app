package com.example.bookbnb.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookbnb.models.Publicacion

class PublicacionesViewModel : ViewModel()  {

    private val _publicaciones = MutableLiveData<List<Publicacion>>()

    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones


    private val _navigateToNewPublicacion = MutableLiveData<Boolean>(false)
    val navigateToNewPublicacion : MutableLiveData<Boolean>
        get() = _navigateToNewPublicacion

    init{
        //_publicaciones.value = listOf(publicacion, publicacion2)
    }

    fun navigateToNuevaPublicacion(){
        _navigateToNewPublicacion.value = true
    }

    fun onDoneNavigatingToNuevaPublicacion(){
        _navigateToNewPublicacion.value = false
    }
}