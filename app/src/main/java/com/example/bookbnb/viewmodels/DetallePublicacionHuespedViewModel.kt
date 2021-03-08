package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Pregunta
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ReservarPublicacionResponse
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class DetallePublicacionHuespedViewModel(application: Application) : DetallePublicacionViewModel(application){
    private val _navigateToChat = MutableLiveData<Boolean>(false)
    val navigateToChat : MutableLiveData<Boolean>
        get() = _navigateToChat

    private val _navigateToPerfil = MutableLiveData<Boolean>(false)
    val navigateToPerfil : MutableLiveData<Boolean>
        get() = _navigateToPerfil

    private val _displayDisponibilidadDialog = MutableLiveData<Boolean>(false)
    val displayDisponibilidadDialog : MutableLiveData<Boolean>
        get() = _displayDisponibilidadDialog

    private val _startDate = MutableLiveData<Date>()
    val startDate : MutableLiveData<Date>
        get() = _startDate

    private val _endDate = MutableLiveData<Date>()
    val endDate : MutableLiveData<Date>
        get() = _endDate

    private val _reservaPrecioTotal = MutableLiveData<Float>()
    val reservaPrecioTotal : MutableLiveData<Float>
        get() = _reservaPrecioTotal

    private val _reservaRealizadaId = MutableLiveData<String>()
    val reservaRealizadaId : MutableLiveData<String>
        get() = _reservaRealizadaId

    private val _navigateToReservationComplete = MutableLiveData<Boolean>(false)
    val navigateToReservationComplete : MutableLiveData<Boolean>
        get() = _navigateToReservationComplete

    private val _showReservaDialog = MutableLiveData<Boolean>(false)
    val showReservaDialog : MutableLiveData<Boolean>
        get() = _showReservaDialog

    private val _pregunta = MutableLiveData<String>()
    val pregunta : MutableLiveData<String>
        get() = _pregunta

    fun setReservaTotalPrice(){
        val diff = endDate.value!!.time - startDate.value!!.time
        val nights = TimeUnit.MILLISECONDS.toDays(diff)
        _reservaPrecioTotal.value = nights * publicacion.value!!.precioPorNoche
    }

    fun onReservaButtonClick(){
        _displayDisponibilidadDialog.value = true
    }

    fun onDoneReservaButtonClick(){
        _displayDisponibilidadDialog.value = false
    }

    fun setDisponibilidadElegida(start: Long, end: Long){
        startDate.value = Date(start)
        endDate.value = Date(end)
    }

    fun showReservaDialog(){
        _showReservaDialog.value = true
    }

    fun onDoneShowingReservaConfirm(){
        _showReservaDialog.value = false
    }

    fun cancelReservation(){
        startDate.value = null
        endDate.value = null
    }

    fun endReservation(){
        viewModelScope.launch {
            try {
                showLoadingSpinner(false)
                val reservaResponse = BookBnBApi(getApplication()).reservarPublicacion(publicacion.value!!.id!!,
                    startDate.value!!,
                    endDate.value!!)
                when (reservaResponse) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                        R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(reservaResponse)
                    is ResultWrapper.Success -> onReservaSuccess(reservaResponse)
                }
            }
            finally {
                hideLoadingSpinner()
            }
        }
    }

    private fun onReservaSuccess(reservaResponse: ResultWrapper.Success<ReservarPublicacionResponse>) {
        _reservaRealizadaId.value = reservaResponse.value.id
        _navigateToReservationComplete.value = true
    }

    fun onDoneNavigatingToReservationComplete(){
        _navigateToReservationComplete.value = false
    }

    fun onPreguntarButtonClick(){
        if (pregunta.value.isNullOrEmpty()){
            showSnackbarErrorMessage("¡Oops! No se agregó ningún texto a la pregunta.")
            return
        }
        viewModelScope.launch {
            try {
                showLoadingSpinner(false)
                val preguntaResponse = BookBnBApi(getApplication()).realizarPregunta(
                    publicacion.value!!.id!!,
                    pregunta.value!!
                )
                when (preguntaResponse) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                        getApplication<Application>().getString(
                            R.string.network_error_msg
                        )
                    )
                    is ResultWrapper.GenericError -> showGenericError(preguntaResponse)
                    is ResultWrapper.Success -> onPreguntaSuccess(preguntaResponse)
                }
            } finally {
                hideLoadingSpinner()
            }
        }
    }

    private fun onPreguntaSuccess(preguntaResponse: ResultWrapper.Success<Pregunta>) {
        showSnackbarSuccessMessage("¡Su pregunta fue realizada con éxito!")
        _pregunta.value = null
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                loadPreguntas(publicacion.value?.id!!)
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    fun onNavigateToPerfil(){
        _navigateToPerfil.value = true
    }

    fun onDoneNavigatingToPerfil(){
        _navigateToPerfil.value = false
    }

    fun onNavigateToChat(){
        _navigateToChat.value = true
    }

    fun onEndNavigatingToChat(){
        _navigateToChat.value = false
    }

    fun hasDisponibilidadElegida(): Boolean {
        return _startDate.value != null && _endDate.value != null
    }
}

class DetallePublicacionHuespedViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetallePublicacionHuespedViewModel::class.java)) {
            return DetallePublicacionHuespedViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}