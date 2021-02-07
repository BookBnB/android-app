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

    val reservas : MutableLiveData<List<Reserva>>
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
            var reservasAux: List<Reserva> = listOf <Reserva>()
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
                is ResultWrapper.Success -> {
                    reservasAux = reservasResponse.value.filter { it.estado == estadoReserva }
                }
            }
            if (reservasAux.isNotEmpty()){
                val idUsuarios = reservasAux.map { it.huespedId }
                val usuarios = getNombresUsuarios(idUsuarios)
                setNombreHuespedes(usuarios, reservasAux)
            }
            reservas.value = reservasAux
        }
    }

    private suspend fun getNombresUsuarios(idUsuarios: List<String>) : List<Usuario>
    {
        var usuarios: List<Usuario> = listOf<Usuario>()
        if (idUsuarios.isNotEmpty()) {
            when (val usuariosResponse =
                BookBnBApi(getApplication()).getUsersInfoById(idUsuarios.distinct())) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(usuariosResponse)
                is ResultWrapper.Success -> usuarios = usuariosResponse.value
            }
        }
        return usuarios
    }

    fun setNombreHuespedes(usuarios: List<Usuario>, reservasAux: List<Reserva>) {
        val usuariosById = usuarios.map { it.id to it }.toMap()
        reservasAux.forEach { it.nombreHuesped = usuariosById[it.huespedId]?.name + " " +  usuariosById[it.huespedId]?.surname}
    }

    fun onDoneShowingConfirmacionReserva() {
        _showConfirmacionReserva.value = false
    }

    fun onDoneShowingReservaAceptada() {
        _showReservaAceptada.value = false
    }

    fun onAceptacionReserva(reservaId: String) {
        // First set the ultima reserva aceptada value so it does not crash later
        _ultimaReservaAceptada.value = reservaId
        showConfirmacionReserva.value = true
    }

}