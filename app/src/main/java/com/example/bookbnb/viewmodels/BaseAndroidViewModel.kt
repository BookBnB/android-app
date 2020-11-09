package com.example.bookbnb.viewmodels

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookbnb.R
import com.example.bookbnb.network.ResultWrapper

open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    protected val _showLoadingSpinner = MutableLiveData<Boolean>(false)
    val showLoadingSpinner: LiveData<Boolean>
        get() = _showLoadingSpinner

    protected val _snackbarMessage = MutableLiveData<String?>(null)
    val snackbarMessage: LiveData<String?>
        get() = _snackbarMessage

    protected val _toastMessage = MutableLiveData<String?>(null)
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    fun showGenericError(response: ResultWrapper.GenericError){
        if (response.error != null){
            showSnackbarMessage("Error: ${response.error.message}")
        }
        else{
            showSnackbarMessage("Error: ${getApplication<Application>().getString(R.string.error_inesperado_txt)}")
        }
    }

    fun showSnackbarMessage(msg: String){
        _snackbarMessage.value = msg
    }

    fun onDoneShowingSnackbarMessage(){
        _snackbarMessage.value = null
    }
}