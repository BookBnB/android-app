package com.example.bookbnb.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.PublicacionDTO
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class ResultadosBusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicaciones = MutableLiveData<List<PublicacionDTO>>()

    val publicaciones : LiveData<List<PublicacionDTO>>
        get() = _publicaciones

    private val _publicacionActual = MutableLiveData<PublicacionDTO>()
    val publicacionActual : MutableLiveData<PublicacionDTO>
        get() = _publicacionActual


    fun setPublicaciones(publicaciones: List<PublicacionDTO>) {
        _publicaciones.value = publicaciones
    }

    suspend fun onGetDetail(publicacionId: String) {
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

    private fun onDetailSuccess(publicacionResponse: ResultWrapper.Success<PublicacionDTO>) {
        _publicacionActual.value = publicacionResponse.value
    }

}

class ResultadosBusquedaViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultadosBusquedaViewModel::class.java)) {
            return ResultadosBusquedaViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}