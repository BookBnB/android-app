package com.example.bookbnb.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeHuespedViewModel : ViewModel() {

    private val _navigateToBusqueda = MutableLiveData<Boolean>(false)
    val navigateToBusqueda : LiveData<Boolean>
        get() = _navigateToBusqueda

    fun navigateToBusqueda(){
        _navigateToBusqueda.value = true
    }

    fun onDoneNavigatingToBusqueda(){
        _navigateToBusqueda.value = false
    }

}