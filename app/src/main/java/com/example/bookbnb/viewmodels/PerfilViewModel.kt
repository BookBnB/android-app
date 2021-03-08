package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _user = MutableLiveData<User>()
    val user : MutableLiveData<User>
        get() = _user

    private val _navigateToEditarPerfil = MutableLiveData<Boolean>(false)
    val navigateToEditarPerfil : MutableLiveData<Boolean>
        get() = _navigateToEditarPerfil

    private val _navigateToPerfil = MutableLiveData<Boolean>(false)
    val navigateToPerfil : MutableLiveData<Boolean>
        get() = _navigateToPerfil

    val formErrors = ObservableArrayList<FormErrors>()

    enum class FormErrors {
        MISSING_NOMBRE,
        MISSING_APELLIDO
    }

    fun isFormValid(): Boolean {
        formErrors.clear()
        if (_user.value?.name.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_NOMBRE)
        }
        if (_user.value?.surname.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_APELLIDO)
        }
        return formErrors.isEmpty()
    }

    fun onSavePerfilClick(){
        viewModelScope.launch {
            try {
                showLoadingSpinner(false)
                if (isFormValid()) {
                    when (val editPerfilResponse = BookBnBApi(getApplication()).editPerfil(user.value!!)) {
                        is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                            getApplication<Application>().getString(
                                R.string.network_error_msg
                            )
                        )
                        is ResultWrapper.GenericError -> showGenericError(editPerfilResponse)
                        is ResultWrapper.Success -> onDoneSavingPerfil()
                    }
                }
            } finally {
                hideLoadingSpinner()
            }
        }
    }

    fun onDoneSavingPerfil(){
        showSnackbarSuccessMessage("El perfil fue guardado correctamente")
        _navigateToPerfil.value = true
    }

    fun onDoneNavigatingToPerfil(){
        _navigateToPerfil.value = false
    }

    fun onNavigateToEditarPerfil(){
        _navigateToEditarPerfil.value = true
    }

    fun onDoneNavigatingToEditarPerfil(){
        _navigateToEditarPerfil.value = false
    }

    fun setUser(userId: String){
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                when (val userResponse = BookBnBApi(getApplication()).getUser(userId)) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(
                        R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(userResponse)
                    is ResultWrapper.Success -> _user.value = userResponse.value
                }
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }
}

class PerfilViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}