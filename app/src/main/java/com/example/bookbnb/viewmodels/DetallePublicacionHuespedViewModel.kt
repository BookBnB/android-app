package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ReservarPublicacionResponse
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class DetallePublicacionHuespedViewModel(application: Application) : DetallePublicacionViewModel(application){
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
                _showLoadingSpinner.value = true
                val reservaResponse = BookBnBApi(getApplication()).reservarPublicacion(publicacion.value!!.id!!,
                    startDate.value!!,
                    endDate.value!!)
                when (reservaResponse) {
                    is ResultWrapper.NetworkError -> showSnackbarMessage(getApplication<Application>().getString(
                        R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(reservaResponse)
                    is ResultWrapper.Success -> onReservaSuccess(reservaResponse)
                }
            }
            finally {
                _showLoadingSpinner.value = false
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