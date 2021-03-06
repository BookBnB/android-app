package com.example.bookbnb.viewmodels

import android.app.Application
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _email = MutableLiveData<String>("")
    val email: MutableLiveData<String>
        get() = _email

    private val _navigateToSucess = MutableLiveData<Boolean>(false)
    val navigateToSucess: MutableLiveData<Boolean>
        get() = _navigateToSucess

    fun onSendEmailRecuperacion(){
        viewModelScope.launch {
            try {
                showLoadingSpinner(false)
                when (val recuperacionResponse =
                    BookBnBApi(getApplication()).sendEmailRecuperacion(email.value!!)) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                        getApplication<Application>().getString(
                            R.string.network_error_msg
                        )
                    )
                    is ResultWrapper.GenericError -> showGenericError(recuperacionResponse)
                    is ResultWrapper.Success -> {
                        // TODO: Show other fragment
                        onNavigateToEmailSent()
                    }
                }
            }
            finally {
                hideLoadingSpinner()
            }
        }
    }

    fun onNavigateToEmailSent(){
        _navigateToSucess.value = true
    }

    fun onDoneNavigatingToEmailSent(){
        _navigateToSucess.value = false
    }
}



class ForgotPasswordViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}