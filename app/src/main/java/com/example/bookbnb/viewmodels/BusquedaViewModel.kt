package com.example.bookbnb.viewmodels

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Publicacion
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

    private val _navigateToSearchResults = MutableLiveData<Boolean>(false)
    val navigateToSearchResults: LiveData<Boolean>
        get() = _navigateToSearchResults

    fun onNavigateToSearchResults() {
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
                    BookBnBApi(getApplication()).getLocations(_destino.value!!)) {
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
        //viewModelScope.launch {

            onNavigateToSearchResults()
            /*val publicacion: Publicacion = Publicacion(1, "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
            val publicacion2: Publicacion = Publicacion(1, "Test", "Desc", "https://live.staticflickr.com/5724/30787745771_31ee1eb522_k.jpg", 100f, "Algun lado","","")
            val searchResponse = listOf(publicacion, publicacion2)
            onSearchSuccess(searchResponse)
            val searchResponse = BookBnBApi(getApplication()).search(_destino.value!!)
            when (searchResponse) {
                is ResultWrapper.NetworkError -> showSnackbarMessage(
                    getApplication<Application>().getString(
                        R.string.network_error_msg
                    )
                )
                is ResultWrapper.GenericError -> showGenericError(searchResponse)
                is ResultWrapper.Success -> onSearchSuccess(searchResponse)
            }
        }*/
        //}

        /*private fun onSearchSuccess(searchResponse: ResultWrapper.Success<List<Publicacion>>) {
        onNavigateToSearchResults()
    }*/

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