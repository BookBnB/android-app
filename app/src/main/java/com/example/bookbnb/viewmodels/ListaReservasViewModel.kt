package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.models.Usuario
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch

class ListaReservasViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _reservas = MutableLiveData<List<Reserva>>()

    val reservas : LiveData<List<Reserva>>
        get() = _reservas

    private val _showConfirmacionReserva = MutableLiveData<Boolean>(false)
    val showConfirmacionReserva: MutableLiveData<Boolean>
        get() = _showConfirmacionReserva

    private val _showReservaAceptada = MutableLiveData<Boolean>(false)
    val showReservaAceptada: MutableLiveData<Boolean>
        get() = _showReservaAceptada

    private val _ultimaReservaAceptada = MutableLiveData<String>()
    val ultimaReservaAceptada: MutableLiveData<String>
        get() = _ultimaReservaAceptada

    fun setReservasByEstado(estadoReserva: String, reservas: List<Reserva>){
        _reservas.value = reservas.filter { it.estado == estadoReserva }
        val idUsuarios = reservas.map{it.huespedId}
        onGetUserInfoById(idUsuarios)
    }

    fun confirmarReserva() {
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())
            when (val reservasResponse =
                BookBnBApi(getApplication()).aceptarReserva(
                    ultimaReservaAceptada.value!!
                )) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(reservasResponse)
                is ResultWrapper.Success -> onReservaAceptada()
            }
        }
    }

    fun onReservaAceptada() {
        showReservaAceptada.value = true
    }

    fun cerrarDialog(){
        ultimaReservaAceptada.value = null
    }

    fun onGetReservas(publicacionId: String, estadoReserva: String) {
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())
            when (val reservasResponse =
                    BookBnBApi(getApplication()).getReservasByPublicacionId(
                        publicacionId
                    )) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(reservasResponse)
                is ResultWrapper.Success -> setReservasByEstado(estadoReserva, reservasResponse.value)
            }
        }
    }

    fun setNombreHuesped(usuarios: List<Usuario>) {
        val usuariosById = usuarios.map { it.id to it }.toMap()

        reservas.value?.forEach { it.nombreHuesped = usuariosById[it.huespedId]?.name + " " +  usuariosById[it.huespedId]?.surname}

    }

    fun onGetUserInfoById(idUsuarios: List<String>) {
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())
            when (val usuariosResponse =
                BookBnBApi(getApplication()).getUsersInfoById(
                    idUsuarios.joinToString(separator = "&id=")
                )) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(usuariosResponse)
                is ResultWrapper.Success -> setNombreHuesped(usuariosResponse.value)
            }
        }
    }

    fun onDoneShowingConfirmacionReserva() {
        _showConfirmacionReserva.value = false
    }

    fun onDoneShowingReservaAceptada() {
        _showReservaAceptada.value = false
    }

    fun onAceptacionReserva(reservaId: String) {
        showConfirmacionReserva.value = true
        _ultimaReservaAceptada.value = reservaId
    }

}