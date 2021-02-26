package com.example.bookbnb.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.models.Usuario
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HuespedReservasViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _reservas = MutableLiveData<List<ReservaVM>>()
    val reservas : MutableLiveData<List<ReservaVM>>
        get() = _reservas

    private val _reservasProximas = MutableLiveData<List<ReservaVM>>()
    val reservasProximas : MutableLiveData<List<ReservaVM>>
        get() = _reservasProximas

    private val _reservasAnteriores = MutableLiveData<List<ReservaVM>>()
    val reservasAnteriores : MutableLiveData<List<ReservaVM>>
        get() = _reservasAnteriores

    fun setSelectedReservasList(selectedList: String){
        reservas.value = if (selectedList == getApplication<Application>().getString(R.string.reservas_proximas_tab_text)){
            reservasProximas.value
        }
        else{
            reservasAnteriores.value
        }
    }

    fun fetchReservasList(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val reservasAux = fetchReservasFromServer()
            if (reservasAux.isNotEmpty()){
                val idPublicaciones = reservasAux.map { it.publicacionId }
                val reservasCalificadas = SessionManager(getApplication()).getReservasCalificadas()
                val publicaciones = getNombresPublicaciones(idPublicaciones)
                val publicacionesById = publicaciones.map { it.id to it }.toMap()
                val reservasVMList = reservasAux.map {
                    ReservaVM(it, publicacionesById[it.publicacionId]?.titulo!!, MutableLiveData(reservasCalificadas.contains(it.id)))
                }
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val (anteriores, proximas) = reservasVMList.partition { it.reserva.isFinished() }
                reservasProximas.value = proximas.sortedBy { format.parse(it.reserva.fechaInicio)!!.time }
                reservasAnteriores.value = anteriores.sortedByDescending { format.parse(it.reserva.fechaFin)!!.time }
            }
            onSuccess()
        }
    }

    private suspend fun fetchReservasFromServer(): List<Reserva> {
        var reservasAux: List<Reserva> = listOf<Reserva>()
        when (val reservasResponse =
            SessionManager(getApplication()).getUserId()?.let {
                BookBnBApi(getApplication()).getReservasByUserId(it)
            }) {
            is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                getApplication<Application>().getString(
                    R.string.network_error_msg
                )
            )
            is ResultWrapper.GenericError -> showGenericError(reservasResponse)
            is ResultWrapper.Success -> {
                reservasAux = reservasResponse.value
            }
        }
        return reservasAux
    }

    private suspend fun getNombresPublicaciones(idPublicaciones: List<String>) : List<Publicacion>
    {
        var publicaciones: List<Publicacion> = listOf<Publicacion>()
        if (idPublicaciones.isNotEmpty()) {
            when (val publicacionesResponse =
                BookBnBApi(getApplication()).getPublicacionesInfoById(idPublicaciones.distinct())) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(publicacionesResponse)
                is ResultWrapper.Success -> publicaciones = publicacionesResponse.value
            }
        }
        return publicaciones
    }

    fun enviarCalificacion(reserva: Reserva, rating: Float, resenia: String) {
        viewModelScope.launch {
            when (val reservasResponse =BookBnBApi(getApplication()).calificarPublicacion(reserva.publicacionId, rating, resenia)) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(reservasResponse)
                is ResultWrapper.Success -> {
                    SessionManager(getApplication()).saveReservaCalificada(reserva.id)
                    showSnackbarSuccessMessage("Su calificación fue enviada correctamente. ¡Muchas gracias!")
                }
            }
        }
    }
}