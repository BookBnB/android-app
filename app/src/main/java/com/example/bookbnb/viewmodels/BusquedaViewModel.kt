package com.example.bookbnb.viewmodels

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.Coordenada
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.math.round

class BusquedaViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _publicaciones = MutableLiveData<List<Publicacion>>()
    val publicaciones : LiveData<List<Publicacion>>
        get() = _publicaciones

    private val _publicacionActual = MutableLiveData<Publicacion>()
    val publicacionActual : MutableLiveData<Publicacion>
        get() = _publicacionActual

    private val _selectedLocation = MutableLiveData<CustomLocation>()
    val selectedLocation: MutableLiveData<CustomLocation>
        get() = _selectedLocation

    private val _selectedTipoAlojamiento = MutableLiveData<String>()
    val selectedTipoAlojamiento: MutableLiveData<String>
        get() = _selectedTipoAlojamiento

    private val _selectedCantHuespedes = MutableLiveData<Int>(1)
    val selectedCantHuespedes: MutableLiveData<Int>
        get() = _selectedCantHuespedes

    private val _selectedMinPrice = MutableLiveData<Float>(0f)
    val selectedMinPrice : MutableLiveData<Float>
        get() = _selectedMinPrice

    private val _selectedMaxPrice = MutableLiveData<Float>(0.5f)
    val selectedMaxPrice : MutableLiveData<Float>
        get() = _selectedMaxPrice

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

    private val _startDate = MutableLiveData<Date>()
    val startDate : MutableLiveData<Date>
        get() = _startDate

    private val _endDate = MutableLiveData<Date>()
    val endDate : MutableLiveData<Date>
        get() = _endDate

    private val _displayDatePickerDialog = MutableLiveData<Boolean>(false)
    val displayDatePickerDialog : MutableLiveData<Boolean>
        get() = _displayDatePickerDialog

    fun resetResults(){
        _publicaciones.value = null
    }

    fun getResults() {
        if (_publicaciones.value == null) {
            viewModelScope.launch {

                when (val searchResponse = BookBnBApi(getApplication()).searchPublicaciones(
                    _selectedLocation.value!!.coordenadas,
                    _selectedTipoAlojamiento.value!!,
                    _selectedCantHuespedes.value!!,
                    _selectedMinPrice.value!!,
                    selectedMaxPrice.value!!,
                    _startDate.value,
                    _endDate.value
                )) {
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
                        getApplication<Application>().getString(
                            R.string.network_error_msg
                        )
                    )
                    is ResultWrapper.GenericError -> showGenericError(searchResponse)
                    is ResultWrapper.Success -> onSearchSuccess(searchResponse)
                }
            }
        }
    }

    private fun onSearchSuccess(searchResponse: ResultWrapper.Success<List<Publicacion>>) {
        _publicaciones.value = searchResponse.value
    }

    fun onNavigateToSearchResults() {
        _navigateToSearchResults.value = true
    }

    fun onDoneNavigateToSearchResults() {
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
                    is ResultWrapper.NetworkError -> showSnackbarErrorMessage(
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
            showSnackbarErrorMessage("No ingresó una ciudad para la búsqueda.")
        }
    }

    fun updateSelectedPrice(values: MutableList<Float>){
        _selectedMinPrice.value = values.min()?.round(4)?.toFloat()
        _selectedMaxPrice.value = values.max()?.round(4)?.toFloat()
    }

    fun setDisponibilidadElegida(start: Long?, end: Long?){
        _startDate.value = if (start != null) Date(start) else null
        _endDate.value = if (end != null) Date(end) else null
    }

    fun displayDatePickerDialog(){
        _displayDatePickerDialog.value = true
    }

    fun onDoneDisplayingDatePickerDialog() {
        _displayDatePickerDialog.value = false
    }

}
fun Float.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
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