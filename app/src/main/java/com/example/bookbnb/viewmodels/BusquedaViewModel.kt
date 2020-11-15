package com.example.bookbnb.viewmodels

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.PublicacionDTO
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

    private val _navigateToSearchResults = MutableLiveData<Boolean>(false)
    val navigateToSearchResults: MutableLiveData<Boolean>
        get() = _navigateToSearchResults

    private val _publicaciones = MutableLiveData<List<PublicacionDTO>>()
    val publicaciones: MutableLiveData<List<PublicacionDTO>>
        get() = _publicaciones

    fun onNavigateToSearchResults(publicaciones : List<PublicacionDTO>) {
        _publicaciones.value = publicaciones
        _navigateToSearchResults.value = true
    }

    fun onDoneNavigateToSearchResults() {
        _navigateToSearchResults.value = false
    }

    fun setSelectedLocation(location: CustomLocation?) {
        _selectedLocation.value = location
    }

    fun searchLocation(name: String) {
        viewModelScope.launch {
            try {
                when (val locationsResponse =
                    BookBnBApi(getApplication()).getCities(_destino.value!!)) {
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
        viewModelScope.launch {

            when (val searchResponse = BookBnBApi(getApplication()).searchByCityCoordinates(
                _selectedLocation.value!!.coordenadas
            )) {
                is ResultWrapper.NetworkError -> showSnackbarMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(searchResponse)
                is ResultWrapper.Success -> onSearchSuccess(searchResponse)
            }
        }
    }

    private fun onSearchSuccess(searchResponse: ResultWrapper.Success<List<PublicacionDTO>>) {
        onNavigateToSearchResults(searchResponse.value)
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