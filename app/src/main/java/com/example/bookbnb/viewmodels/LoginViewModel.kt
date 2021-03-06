package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.LoginResponse
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

class LoginViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _username = MutableLiveData<String>("")
    val username: MutableLiveData<String>
        get() = _username

    private val _password = MutableLiveData<String>("")
    val password: MutableLiveData<String>
        get() = _password

    private val _navigateToMainActivity = MutableLiveData<Boolean>(false)
    val navigateToMainActivity: LiveData<Boolean>
        get() = _navigateToMainActivity

    private val _navigateToRegister = MutableLiveData<Boolean>(false)
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    private val _navigateToForgotPassword = MutableLiveData<Boolean>(false)
    val navigateToForgotPassword: LiveData<Boolean>
        get() = _navigateToForgotPassword

    private val _showGoogleSignIn = MutableLiveData<Boolean>(false)
    val showGoogleSignIn: LiveData<Boolean>
        get() = _showGoogleSignIn

    private val _showGoogleSignUp = MutableLiveData<Boolean>(false)
    val showGoogleSignUp: LiveData<Boolean>
        get() = _showGoogleSignUp

    private val _googleToken = MutableLiveData<String>("")
    val googleToken: MutableLiveData<String>
        get() = _googleToken

    fun onShowGoogleSignInClick(){
        _showGoogleSignIn.value = true
    }

    fun onDoneShowingGoogleSignInClick(){
        _showGoogleSignIn.value = false
    }

    fun onDoneShowingGoogleSignUpWithGoogle(){
        _showGoogleSignUp.value = false
    }

    fun onNavigateToForgotPassword(){
        _navigateToForgotPassword.value = true //Trigger navigate to main activity
    }

    fun onDoneNavigatingToForgotPassword(){
        _navigateToForgotPassword.value = false
    }

    fun onNavigateToRegister(){
        _navigateToRegister.value = true //Trigger navigate to main activity
    }

    fun onDoneNavigateToRegister(){
        _navigateToRegister.value = false
    }

    fun onLogin(){
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                when (val loginResponse = BookBnBApi(getApplication()).authenticate(username.value!!, password.value!!)) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(loginResponse)
                    is ResultWrapper.Success -> onLoginSuccess(loginResponse)
                }
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    fun onGoogleLogin(token: String){
        _googleToken.value = token
        viewModelScope.launch {
            try {
                _showLoadingSpinner.value = true
                when (val loginResponse = BookBnBApi(getApplication()).authenticate(token)) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(R.string.network_error_msg))
                    is ResultWrapper.GenericError -> {
                        if (loginResponse.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            _showGoogleSignUp.value = true
                        }
                        else{
                            showGenericError(loginResponse)
                        }
                    }
                    is ResultWrapper.Success -> onLoginSuccess(loginResponse)
                }
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    private fun onLoginSuccess(loginResponse: ResultWrapper.Success<LoginResponse>) {
        val sessionManager = SessionManager(getApplication())
        sessionManager.saveAuthToken(loginResponse.value.token)
        _navigateToMainActivity.value = true
    }
}

class LoginViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

