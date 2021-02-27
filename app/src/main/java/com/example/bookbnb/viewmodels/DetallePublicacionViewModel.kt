package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Calificacion
import com.example.bookbnb.models.Pregunta
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.models.Usuario
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

open class DetallePublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _publicacion = MutableLiveData<Publicacion>()

    val publicacion : LiveData<Publicacion>
        get() = _publicacion

    private val _calificaciones = MutableLiveData<List<CalificacionVM>>()
    val calificaciones : MutableLiveData<List<CalificacionVM>>
        get() = _calificaciones

    private val _preguntas = MutableLiveData<List<Pregunta>>()
    val preguntas : MutableLiveData<List<Pregunta>>
        get() = _preguntas

    private val _selectedPregunta = MutableLiveData<Pregunta>()
    val selectedPregunta : MutableLiveData<Pregunta>
        get() = _selectedPregunta

    private val _selectedPreguntaRespuesta = MutableLiveData<String>()
    val selectedPreguntaRespuesta : MutableLiveData<String>
        get() = _selectedPreguntaRespuesta

    private val _navigateToReservationList = MutableLiveData<Boolean>(false)
    val navigateToReservationList : MutableLiveData<Boolean>
        get() = _navigateToReservationList

    fun onGetDetail(publicacionId: String) {
        viewModelScope.launch {
            try {
                showLoadingSpinner()
                loadPublicacion(publicacionId)
                loadPreguntas(publicacionId)
                loadCalificaciones(publicacionId)
            }
            finally {
                hideLoadingSpinner()
            }
        }
    }

    fun onVerReservasButtonClick() {
        _navigateToReservationList.value = true
    }

    fun onDoneNavigatingToReservationList(){
        _navigateToReservationList.value = false
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

    protected suspend fun loadCalificaciones(publicacionId: String){
        when (val calificacionesResponse = BookBnBApi(getApplication()).getCalificaciones(publicacionId)){
            is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                R.string.network_error_msg))
            is ResultWrapper.GenericError -> showGenericError(calificacionesResponse)
            is ResultWrapper.Success -> onGetCalificacionesSuccess(calificacionesResponse)
        }
    }

    private suspend fun getNombresUsuarios(idUsuarios: List<String>) : List<Usuario>
    {
        var usuarios: List<Usuario> = listOf<Usuario>()
        if (idUsuarios.isNotEmpty()) {
            when (val usuariosResponse =
                BookBnBApi(getApplication()).getUsersInfoById(idUsuarios.distinct())) {
                is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(usuariosResponse)
                is ResultWrapper.Success -> usuarios = usuariosResponse.value
            }
        }
        return usuarios
    }


    private suspend fun onGetCalificacionesSuccess(calificacionesResponse: ResultWrapper.Success<List<Calificacion>>) {
        calificacionesResponse.value.let { it ->
            val idUsuarios = it.map { it.huespedId!! }
            val usuarios = getNombresUsuarios(idUsuarios)
            val usuariosById = usuarios.map { it.id to it }.toMap()
            _calificaciones.value = it.map {
                CalificacionVM(
                    it.puntos.toInt(),
                    it.detalle,
                    "${usuariosById[it.huespedId]?.name} ${usuariosById[it.huespedId]?.surname}"
                )
            }
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
                showLoadingSpinner(false)
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
                hideLoadingSpinner()
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
