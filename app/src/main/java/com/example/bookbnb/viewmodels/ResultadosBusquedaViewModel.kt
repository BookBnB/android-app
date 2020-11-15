package com.example.bookbnb.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Coordenada
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

    fun getResults(coordenadas: Coordenada) {
        viewModelScope.launch {

            when (val searchResponse = BookBnBApi(getApplication()).searchByCityCoordinates(
                coordenadas
            )) {
                is ResultWrapper.NetworkError -> showSnackbarMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(searchResponse)
                is ResultWrapper.Success -> onSearchSuccess(searchResponse)
            }
        }
    }

    private fun onSearchSuccess(searchResponse: ResultWrapper.Success<List<PublicacionDTO>>) {
        _publicaciones.value = searchResponse.value
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