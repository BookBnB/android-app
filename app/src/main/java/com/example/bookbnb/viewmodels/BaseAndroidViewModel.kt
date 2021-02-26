package com.example.bookbnb.viewmodels

import android.app.Application
import android.graphics.Color
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

    protected val _hideBackgroundView = MutableLiveData<Boolean>(true)
    val hideBackgroundView: LiveData<Boolean>
        get() = _hideBackgroundView

    protected val _snackbarMessage = MutableLiveData<String?>(null)
    val snackbarMessage: LiveData<String?>
        get() = _snackbarMessage

    val _snackbarColor = MutableLiveData<Int>(R.color.black)
    val snackbarColor: LiveData<Int>
        get() = _snackbarColor

    protected val _toastMessage = MutableLiveData<String?>(null)
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    fun showGenericError(response: ResultWrapper.GenericError){
        _snackbarColor.value = R.color.error
        if (response.error != null){
            showSnackbarErrorMessage("Error: ${response.error.message}")
        }
        else{
            showSnackbarErrorMessage("Error: ${getApplication<Application>().getString(R.string.error_inesperado_txt)}")
        }
    }

    fun showLoadingSpinner(hideBackgroundView: Boolean = true){
        _hideBackgroundView.value = hideBackgroundView
        _showLoadingSpinner.value = true
    }

    fun hideLoadingSpinner(){
        _hideBackgroundView.value = true // Por defecto true
        _showLoadingSpinner.value = false
    }

    fun showSnackbarErrorMessage(msg: String){
        _snackbarColor.value = R.color.error
        _snackbarMessage.value = msg
    }

    fun showSnackbarSuccessMessage(msg: String){
        _snackbarColor.value = R.color.success
        _snackbarMessage.value = msg
    }

    fun showSnackbarMessage(msg: String){
        _snackbarColor.value = R.color.black
        _snackbarMessage.value = msg
    }

    fun onDoneShowingSnackbarMessage(){
        _snackbarMessage.value = null
    }
}