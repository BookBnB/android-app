package com.example.bookbnb.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.bookbnb.R
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


    init{
        val publicacion: Publicacion = Publicacion("1", "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
        val publicacion2: Publicacion = Publicacion("2", "Test2", "Desc2", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 300f, "Algun lado 2","","")
        _publicaciones.value = listOf(publicacion, publicacion2)
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

    private fun onDetailSuccess(publicacionResponse: ResultWrapper.Success<Publicacion>) {
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