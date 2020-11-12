package com.example.bookbnb.viewmodels

import android.app.Application
import android.content.ClipData
import android.net.Uri
import android.widget.ArrayAdapter
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import com.example.bookbnb.R
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.ResultWrapper
import kotlinx.coroutines.launch
import java.lang.Float.parseFloat


class NuevaPublicacionViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _titulo = MutableLiveData<String>("")
    val titulo: MutableLiveData<String>
        get() = _titulo

    private val _desc  = MutableLiveData<String>("")
    val desc: MutableLiveData<String>
        get() = _desc

    private val _price = MutableLiveData<String>("")
    val price: MutableLiveData<String>
        get() = _price

    private val _selectedLocation = MutableLiveData<CustomLocation>()
    val selectedLocation: MutableLiveData<CustomLocation>
        get() = _selectedLocation

    private val _locationText = MutableLiveData<String>("")
    val locationText: MutableLiveData<String>
        get() = _locationText

    private val _selectedPhotosUri: MutableLiveData<List<Uri>> = MutableLiveData()
    val selectedPhotosUri: MutableLiveData<List<Uri>>
    get() = _selectedPhotosUri

    private val _autocompleteLocationAdapter = MutableLiveData<ArrayAdapter<CustomLocation?>>()
    val autocompleteLocationAdapter: MutableLiveData<ArrayAdapter<CustomLocation?>>
        get() = _autocompleteLocationAdapter

    private val _navigateToMapStep = MutableLiveData<Boolean>(false)
    val navigateToMapStep: MutableLiveData<Boolean>
        get() = _navigateToMapStep

    private val _navigateToImagesStep = MutableLiveData<Boolean>(false)
    val navigateToImagesStep: MutableLiveData<Boolean>
        get() = _navigateToImagesStep

    private val _initiatePhotoSelection = MutableLiveData<Boolean>(false)
    val initiatePhotoSelection: MutableLiveData<Boolean>
        get() = _initiatePhotoSelection

    private val _selectPhotosError = MutableLiveData<String>("")
    val selectPhotosError: MutableLiveData<String>
        get() = _selectPhotosError

    val formErrors = ObservableArrayList<FormErrors>()

    enum class FormErrors {
        MISSING_TITULO,
        MISSING_DESC,
        INVALID_PRICE
    }

    fun setSelectedLocation(location: CustomLocation?){
        _selectedLocation.value = location
    }

    fun searchLocation(name: String) {
        viewModelScope.launch {
            try {
                when (val locationsResponse = BookBnBApi(getApplication()).getLocations(_locationText.value!!)) {
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

    private fun isFirstStepFormValid(): Boolean{
        formErrors.clear()
        if (_titulo.value.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_TITULO)
        }
        if (_desc.value.isNullOrEmpty()) {
            formErrors.add(FormErrors.MISSING_DESC)
        }
        try{
            parseFloat(_price.value!!)
        }
        catch(e: Exception){
            formErrors.add(FormErrors.INVALID_PRICE)
        }
        return formErrors.isEmpty()
    }

    fun onNavigateToMapStep() {
        if (isFirstStepFormValid()) {
            _navigateToMapStep.value = true
        }
    }

    fun onDoneNavigatingToMapStep(){
        _navigateToMapStep.value = false
    }

    fun onDoneShowingPhotosError(){
        _selectPhotosError.value = ""
    }

    fun onNavigateToImagesStep(){
        if (_selectedLocation.value != null){
            _navigateToImagesStep.value = true
        }
        else{
            showSnackbarMessage(
                getApplication<Application>().getString(
                    R.string.no_location_selected_txt
                )
            )
        }
    }

    fun onDoneNavigatingToImagesStep(){
        _navigateToImagesStep.value = false
    }

    fun onInitiatePhotoSelection(){
        _initiatePhotoSelection.value = true
    }

    fun onDoneInitiatingPhotoSelection(){
        _initiatePhotoSelection.value = false
    }

    fun handlePhotoFinishSelection(clipData: ClipData?, dataUri: Uri?) {
        if (clipData == null && dataUri == null) {
            _selectPhotosError.value = "Debe seleccionar al menos una foto de su alojamiento."
            return
        }
        var selectedPhotos: MutableList<Uri> = ArrayList()
        // When user clipData is null and dataUri is not null, user has selected only one image
        if (clipData == null && dataUri != null) {
            selectedPhotos.add(dataUri)
        }
        else {
            val totalPhotos = clipData!!.itemCount
            if (!isTotalPhotosOnLimits(totalPhotos)) {
                _selectPhotosError.value = "Debe seleccionar al menos 1 y como máximo 5 fotografías de su alojamiento"
                return
            }
            for (i in 0 until totalPhotos) {
                val uri = clipData.getItemAt(i).uri
                selectedPhotos.add(uri)
            }
        }
        _selectedPhotosUri.value = selectedPhotos
    }

    private fun isTotalPhotosOnLimits(totalPhotos: Int) : Boolean{
        return totalPhotos in 1..5
    }

    fun hasSelectedPhotos() : Boolean{
        return _selectedPhotosUri.value != null && _selectedPhotosUri.value!!.isNotEmpty()
    }
}

class NuevaPublicacionViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NuevaPublicacionViewModel::class.java)) {
            return NuevaPublicacionViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}