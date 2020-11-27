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
import java.util.concurrent.TimeUnit
import kotlin.math.round

open class DetallePublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicacion = MutableLiveData<Publicacion>()

    val publicacion : LiveData<Publicacion>
        get() = _publicacion



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
