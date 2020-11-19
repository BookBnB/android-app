package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.LoginResponse
import com.example.bookbnb.network.ReservarPublicacionResponse
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch
import java.lang.Float.parseFloat
import java.util.*

class DetallePublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicacion = MutableLiveData<Publicacion>()

    val publicacion : LiveData<Publicacion>
        get() = _publicacion

    private val _displayDisponibilidadDialog = MutableLiveData<Boolean>(false)
    val displayDisponibilidadDialog : MutableLiveData<Boolean>
        get() = _displayDisponibilidadDialog

    private val _startDate = MutableLiveData<Date>()
    val startDate : MutableLiveData<Date>
        get() = _startDate

    private val _endDate = MutableLiveData<Date>()
    val endDate : MutableLiveData<Date>
        get() = _endDate

    private val _navigateToReservationComplete = MutableLiveData<Boolean>(false)
    val navigateToReservationComplete : MutableLiveData<Boolean>
        get() = _navigateToReservationComplete

    private val _showReservaPrecioDialog = MutableLiveData<Boolean>(false)
    val showReservaPrecioDialog : MutableLiveData<Boolean>
        get() = _showReservaPrecioDialog

    private val _price = MutableLiveData<String>("")
    val price: MutableLiveData<String>
        get() = _price

    fun onGetDetail(publicacionId: String) {
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                when (val publicationResponse = BookBnBApi(getApplication()).getPublicacionById(publicacionId)) {
                    is ResultWrapper.NetworkError -> showSnackbarMessage(getApplication<Application>().getString(
                        R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(publicationResponse)
                    is ResultWrapper.Success -> onDetailSuccess(publicationResponse)
                }
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    private fun onDetailSuccess(publicacionResponse: ResultWrapper.Success<Publicacion>) {
        _publicacion.value = publicacionResponse.value
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
        showReservaPrecioDialog.value = true
    }

    fun onDoneShowingReservaPrecio(){
        showReservaPrecioDialog.value = false
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
                    endDate.value!!,
                    parseFloat(price.value!!)
                )
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
        _navigateToReservationComplete.value = true
    }

    fun onDoneNavigatingToReservationComplete(){
        _navigateToReservationComplete.value = false
    }
}

class DetallePublicacionViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetallePublicacionViewModel::class.java)) {
            return DetallePublicacionViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}