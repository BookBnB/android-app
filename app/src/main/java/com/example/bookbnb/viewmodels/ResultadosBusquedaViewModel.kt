package com.example.bookbnb.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Coordenada
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class ResultadosBusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicaciones = MutableLiveData<List<Publicacion>>()

    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones

    private val _publicacionActual = MutableLiveData<Publicacion>()
    val publicacionActual : MutableLiveData<Publicacion>
        get() = _publicacionActual

    fun getResults(coordenadas: Coordenada,
                   tipoAlojamiento: String,
                   cantHuespedes: Int,
                   minPrice: Float,
                   maxPrice: Float) {
        viewModelScope.launch {

            when (val searchResponse = BookBnBApi(getApplication()).searchPublicaciones(
                coordenadas, tipoAlojamiento, cantHuespedes, minPrice, maxPrice
            )) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(searchResponse)
                is ResultWrapper.Success -> onSearchSuccess(searchResponse)
            }
        }
    }

    private fun onSearchSuccess(searchResponse: ResultWrapper.Success<List<Publicacion>>) {
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