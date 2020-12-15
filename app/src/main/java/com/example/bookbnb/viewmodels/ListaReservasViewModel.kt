package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch

class ListaReservasViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _reservas = MutableLiveData<List<Reserva>>()

    val reservas : LiveData<List<Reserva>>
        get() = _reservas

    init{
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())
            when (val reservasResponse =
                sessionManager.getUserId()?.let {
                    BookBnBApi(getApplication()).getReservasByPublicacionId(
                        it
                    )
                }) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(reservasResponse)
                is ResultWrapper.Success -> _reservas.value = reservasResponse.value
            }
        }
    }

}