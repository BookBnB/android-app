package com.example.bookbnb.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.models.Publicacion

class ResultadosBusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicaciones = MutableLiveData<List<Publicacion>>()

    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones

    private val _publicacionIdActual = MutableLiveData<Int>()
    val publicacionIdActual : MutableLiveData<Int>
        get() = _publicacionIdActual


    init{
        val publicacion: Publicacion = Publicacion("1", "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
        val publicacion2: Publicacion = Publicacion("2", "Test2", "Desc2", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 300f, "Algun lado 2","","")
        _publicaciones.value = listOf(publicacion, publicacion2)
    }

}

class ResultadosBusquedaViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultadosBusquedaViewModel::class.java)) {
            return ResultadosBusquedaViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}