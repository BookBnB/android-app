package com.example.bookbnb.viewmodels

import android.app.Application
import android.content.SharedPreferences
import android.text.TextUtils
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.LoginResponse
import com.example.bookbnb.network.RegisterResponse
import com.example.bookbnb.network.ResultWrapper
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch


class RegisterViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _email = MutableLiveData<String>("")
    val email: MutableLiveData<String>
        get() = _email

    private val _nombre = MutableLiveData<String>("")
    val nombre: MutableLiveData<String>
        get() = _nombre

    private val _apellido = MutableLiveData<String>("")
    val apellido: MutableLiveData<String>
        get() = _apellido

    private val _telefono = MutableLiveData<String>("")
    val telefono: MutableLiveData<String>
        get() = _telefono

    private val _ciudad = MutableLiveData<String>("")
    val ciudad: MutableLiveData<String>
        get() = _ciudad

    private val _userType =
        MutableLiveData<String>(application.getString(R.string.user_type_huesped)) //Defaults to huesped
    val userType: MutableLiveData<String>
        get() = _userType

    private val _password = MutableLiveData<String>("")
    val password: MutableLiveData<String>
        get() = _password

    private val _confirmPassword = MutableLiveData<String>("")
    val confirmPassword: MutableLiveData<String>
        get() = _confirmPassword

    val formErrors = ObservableArrayList<FormErrors>()

    enum class FormErrors {
        MISSING_NOMBRE,
        MISSING_APELLIDO,
        INVALID_EMAIL,
        INVALID_PASSWORD,
        PASSWORDS_NOT_MATCHING,
    }

    private val _navigateToLogin = MutableLiveData<Boolean>(false)
    val navigateToLogin: LiveData<Boolean>
        get() = _navigateToLogin

    fun onNavigateToLogin(){
        _navigateToLogin.value = true
    }

    fun onDoneNavigateToLogin(){
        _navigateToLogin.value = false
    }

    fun isFormValid(): Boolean {
        formErrors.clear()
        if (_nombre.value.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_NOMBRE)
        }
        if (_apellido.value.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_APELLIDO)
        }
        if (_email.value.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value!!)
                .matches()
        ) {
            formErrors.add(FormErrors.INVALID_EMAIL)
        }
        if (_password.value.isNullOrEmpty()) {
            formErrors.add(FormErrors.INVALID_PASSWORD)
        }
        if (_password.value != _confirmPassword.value) {
            formErrors.add(FormErrors.PASSWORDS_NOT_MATCHING)
        }
        return formErrors.isEmpty()
    }

    fun onRegister() {
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                if (isFormValid()) {
                    val registerResponse =
                        BookBnBApi.register(
                            _email.value!!,
                            _password.value!!,
                            _nombre.value!!,
                            _apellido.value!!,
                            _telefono.value,
                            _ciudad.value,
                            _userType.value!!)
                    when (registerResponse) {
                        is ResultWrapper.NetworkError -> showSnackbarMessage(
                            getApplication<Application>().getString(
                                R.string.network_error_msg
                            )
                        )
                        is ResultWrapper.GenericError -> showGenericError(registerResponse)
                        is ResultWrapper.Success -> onRegisterSuccess(registerResponse)
                    }
                }
            } finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    private fun onRegisterSuccess(registerResponse: ResultWrapper.Success<RegisterResponse>) {
        _snackbarMessage.value = "Registro completado. Por favor, ingrese sus crendeciales en el login."
        _navigateToLogin.value = true
    }
}

class RegisterViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
