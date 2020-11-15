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

    private val _publicacionActual = MutableLiveData<Publicacion>()
    val publicacionActual : MutableLiveData<Publicacion>
        get() = _publicacionActual

    private val _navigateToDetallePublicacion = MutableLiveData<Boolean>(false)
    val navigateToDetallePublicacion : MutableLiveData<Boolean>
        get() = _navigateToDetallePublicacion

    private val _navigateToNewPublicacion = MutableLiveData<Boolean>(false)
    val navigateToNewPublicacion : MutableLiveData<Boolean>
        get() = _navigateToNewPublicacion

    init{
        val publicacion: Publicacion = Publicacion(1, "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
        val publicacion2: Publicacion = Publicacion(1, "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
        _publicaciones.value = listOf(publicacion, publicacion2)
    }

    fun navigateToNuevaPublicacion(){
        _navigateToNewPublicacion.value = true
    }

    fun onDoneNavigatingToNuevaPublicacion(){
        _navigateToNewPublicacion.value = false
    }

    fun navigateToDetallePublicacion(publicacion: Publicacion){
        _navigateToDetallePublicacion.value = true
        _publicacionActual.value = publicacion
    }

    fun onDoneNavigateToDetallePublicacion(){
        _navigateToDetallePublicacion.value = false
        _publicacionActual.value = null
    }
}