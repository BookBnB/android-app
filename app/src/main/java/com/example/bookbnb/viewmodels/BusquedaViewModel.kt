package com.example.bookbnb.viewmodels

import android.app.Application
import android.widget.ArrayAdapter
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookbnb.R
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.lang.Exception

class BusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _selectedLocation = MutableLiveData<CustomLocation>()
    val selectedLocation: MutableLiveData<CustomLocation>
        get() = _selectedLocation

    private val _destino = MutableLiveData<String>("")
    val destino: MutableLiveData<String>
        get() = _destino

    private val _autocompleteLocationAdapter = MutableLiveData<ArrayAdapter<CustomLocation?>>()
    val autocompleteLocationAdapter: MutableLiveData<ArrayAdapter<CustomLocation?>>
        get() = _autocompleteLocationAdapter


    fun setSelectedLocation(location: CustomLocation?){
        _selectedLocation.value = location
    }

    fun searchLocation(name: String) {
        viewModelScope.launch {
            try {
                when (val locationsResponse = BookBnBApi(getApplication()).getLocations(_destino.value!!)) {
                    is ResultWrapper.NetworkError -> showSnackbarMessage(
                        getApplication<Application>().getString(
                            R.string.network_error_msg
                        )
                    )
                    is ResultWrapper.GenericError -> showGenericError(locationsResponse)
                    is ResultWrapper.Success -> setAutoCompleteSuggestions(locationsResponse.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setAutoCompleteSuggestions(locationsSuggestions: List<CustomLocation>) {
        _autocompleteLocationAdapter.value = ArrayAdapter(
            getApplication<Application>().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            locationsSuggestions
        )
    }

    fun onGetResults() {

    }

}

class BusquedaViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusquedaViewModel::class.java)) {
            return BusquedaViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}