package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Pregunta
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

    private val _preguntas = MutableLiveData<List<Pregunta>>()
    val preguntas : MutableLiveData<List<Pregunta>>
        get() = _preguntas

    private val _selectedPregunta = MutableLiveData<Pregunta>()
    val selectedPregunta : MutableLiveData<Pregunta>
        get() = _selectedPregunta

    private val _selectedPreguntaRespuesta = MutableLiveData<String>()
    val selectedPreguntaRespuesta : MutableLiveData<String>
        get() = _selectedPreguntaRespuesta

    fun onGetDetail(publicacionId: String) {
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                loadPublicacion(publicacionId)
                loadPreguntas(publicacionId)
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    protected suspend fun loadPublicacion(publicacionId: String){
        when (val publicationResponse = BookBnBApi(getApplication()).getPublicacionById(publicacionId)) {
            is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                R.string.network_error_msg))
            is ResultWrapper.GenericError -> showGenericError(publicationResponse)
            is ResultWrapper.Success -> onDetailSuccess(publicationResponse)
        }
    }

    protected suspend fun loadPreguntas(publicacionId: String){
        when (val preguntasResponse = BookBnBApi(getApplication()).getPreguntas(publicacionId)){
            is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                R.string.network_error_msg))
            is ResultWrapper.GenericError -> showGenericError(preguntasResponse)
            is ResultWrapper.Success -> onGetPreguntasSuccess(preguntasResponse)
        }
    }

    private fun onGetPreguntasSuccess(preguntasResponse: ResultWrapper.Success<List<Pregunta>>) {
        _preguntas.value = preguntasResponse.value.sortedByDescending { p -> p.creada }
    }

    private fun onDetailSuccess(publicacionResponse: ResultWrapper.Success<Publicacion>) {
        _publicacion.value = publicacionResponse.value
    }

    fun cleanRespuesta(){
        _selectedPregunta.value = null
        _selectedPreguntaRespuesta.value = null
    }

    fun onEnviarRespuesta(){
        if (_selectedPregunta.value == null){
            showSnackbarErrorMessage("¡Oops! No se envió la respuesta porque estaba vacía.")
        }
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                val respuestaResponse = BookBnBApi(getApplication()).responderPregunta(publicacion.value?.id!!,
                    _selectedPregunta.value?.id!!,
                    _selectedPreguntaRespuesta.value!!)
                when (respuestaResponse) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                        R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(respuestaResponse)
                    is ResultWrapper.Success -> showSnackbarSuccessMessage("¡Su respuesta fue enviada con éxito!")
                }
                cleanRespuesta()
                loadPreguntas(_publicacion.value?.id!!)
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
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
