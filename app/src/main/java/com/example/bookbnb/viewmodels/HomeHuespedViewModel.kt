package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class HomeHuespedViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _recomendaciones = MutableLiveData<List<Publicacion>>()
    val recomendaciones : LiveData<List<Publicacion>>
        get() = _recomendaciones

    private val _navigateToBusqueda = MutableLiveData<Boolean>(false)
    val navigateToBusqueda : LiveData<Boolean>
        get() = _navigateToBusqueda

    fun navigateToBusqueda(){
        _navigateToBusqueda.value = true
    }

    fun onDoneNavigatingToBusqueda(){
        _navigateToBusqueda.value = false
    }

    fun fetchRecomendaciones() {
        if (_recomendaciones.value == null) {
            viewModelScope.launch {
                try{
                    showLoadingSpinner(false)
                    when (val recomendacionesResponse = BookBnBApi(getApplication()).getRecomendaciones()) {
                        is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                            getApplication<Application>().getString(
                                R.string.network_error_msg
                            )
                        )
                        is ResultWrapper.GenericError -> showGenericError(recomendacionesResponse)
                        is ResultWrapper.Success ->
                            _recomendaciones.value = recomendacionesResponse.value
                    }
                }
                finally {
                    hideLoadingSpinner()
                }
            }
        }
    }
}

class HomeHuespedViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeHuespedViewModel::class.java)) {
            return HomeHuespedViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}