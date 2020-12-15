package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch

class PublicacionesViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicaciones = MutableLiveData<List<Publicacion>>()

    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones


    private val _navigateToNewPublicacion = MutableLiveData<Boolean>(false)
    val navigateToNewPublicacion : MutableLiveData<Boolean>
        get() = _navigateToNewPublicacion

    init{
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())
            when (val publicacionesResponse =
                sessionManager.getUserId()?.let {
                    BookBnBApi(getApplication()).getPublicationsByAnfitrionId(
                        it
                    )
                }) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(publicacionesResponse)
                is ResultWrapper.Success -> _publicaciones.value = publicacionesResponse.value
            }
        }
    }

    fun navigateToNuevaPublicacion(){
        _navigateToNewPublicacion.value = true
    }

    fun onDoneNavigatingToNuevaPublicacion(){
        _navigateToNewPublicacion.value = false
    }
}