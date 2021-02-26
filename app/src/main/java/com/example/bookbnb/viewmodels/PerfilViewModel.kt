package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

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