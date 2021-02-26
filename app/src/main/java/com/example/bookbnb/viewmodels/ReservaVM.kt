package com.example.bookbnb.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.bookbnb.models.Reserva

data class ReservaVM(val reserva: Reserva,
                     val publicacionTitulo: String,
                     private val _isCalificada: MutableLiveData<Boolean> = MutableLiveData(false)) {

    private val _rating = MutableLiveData<Float?>()
    val rating : MutableLiveData<Float?>
        get() = _rating

    private val _resenia = MutableLiveData<String?>()
    val resenia: MutableLiveData<String?>
        get() = _resenia

    val isCalificada : MutableLiveData<Boolean>
        get() = _isCalificada


    fun isGradable() : Boolean{
        return reserva.isGradable() && !isCalificada.value!!
    }
}