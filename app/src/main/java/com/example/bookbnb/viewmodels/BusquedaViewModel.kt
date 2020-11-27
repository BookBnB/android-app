package com.example.bookbnb.viewmodels

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Coordenada
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.lang.Exception

class BusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val _selectedLocation = MutableLiveData<CustomLocation>()
    val selectedLocation: MutableLiveData<CustomLocation>
        get() = _selectedLocation

    private val _selectedTipoAlojamiento = MutableLiveData<String>()
    val selectedTipoAlojamiento: MutableLiveData<String>
        get() = _selectedTipoAlojamiento

    private val _selectedCantHuespedes = MutableLiveData<Int>(1)
    val selectedCantHuespedes: MutableLiveData<Int>
        get() = _selectedCantHuespedes

    private val _destino = MutableLiveData<String>("")
    val destino: MutableLiveData<String>
        get() = _destino

    private val _autocompleteLocationAdapter = MutableLiveData<ArrayAdapter<CustomLocation?>>()
    val autocompleteLocationAdapter: MutableLiveData<ArrayAdapter<CustomLocation?>>
        get() = _autocompleteLocationAdapter

    private val _navigateToSearchResults = MutableLiveData<Boolean>(false)
    val navigateToSearchResults: MutableLiveData<Boolean>
        get() = _navigateToSearchResults

    private val _coordenadas = MutableLiveData<Coordenada>()
    val coordenadas: MutableLiveData<Coordenada>
        get() = _coordenadas

    fun onNavigateToSearchResults() {
        _coordenadas.value = _selectedLocation.value!!.coordenadas
        _navigateToSearchResults.value = true
    }

    fun onDoneNavigateToSearchResults() {
        _coordenadas.value = null
        _navigateToSearchResults.value = false
    }


    fun setSelectedCantHuespedes(cantHuespedes: Int) {
        if (selectedCantHuespedes.value != cantHuespedes) {
            selectedCantHuespedes.value = cantHuespedes
        }
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
        if (selectedLocation.value != null) {
            onNavigateToSearchResults()
        }
        else{
            showSnackbarMessage("No ingresó una ciudad para la búsqueda.")
        }
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