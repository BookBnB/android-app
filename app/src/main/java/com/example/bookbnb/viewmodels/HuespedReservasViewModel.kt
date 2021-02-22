package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch

class HuespedReservasViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _reservas = MutableLiveData<List<Reserva>>()
    val reservas : MutableLiveData<List<Reserva>>
        get() = _reservas

    fun setReservasList() {
        viewModelScope.launch {
            when (val reservasResponse =
                SessionManager(getApplication()).getUserId()?.let {
                    BookBnBApi(getApplication()).getReservasByUserId(
                        it
                    )
                }) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(reservasResponse)
                is ResultWrapper.Success -> reservas.value = reservasResponse.value
            }
        }
    }
}