package com.example.bookbnb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
        showSnackbarMessage(response.error!!.message)
    }

    fun showSnackbarMessage(msg: String){
        _snackbarMessage.value = msg
    }

    fun onDoneShowingSnackbarMessage(){
        _snackbarMessage.value = null
    }
}