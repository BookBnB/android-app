package com.example.bookbnb.viewmodels

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.*
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.LoginResponse
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                when (val loginResponse = BookBnBApi.authenticate(username.value!!, password.value!!)) {
                    is ResultWrapper.NetworkError -> showSnackbarMessage(getApplication<Application>().getString(R.string.network_error_msg))
                    is ResultWrapper.GenericError -> showGenericError(loginResponse)
                    is ResultWrapper.Success -> onLoginSuccess(loginResponse)
                }
            }
            finally {
                _showLoadingSpinner.value = false
            }
        }
    }

    private fun onLoginSuccess(loginResponse: ResultWrapper.Success<LoginResponse>) {
        val pref: SharedPreferences = getApplication<Application>()
            .applicationContext
            .getSharedPreferences("UserPrefs", 0) // 0 - for private mode
        val editor = pref.edit()
        editor.putString("UserToken", loginResponse.value.token)
        editor.commit()
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
