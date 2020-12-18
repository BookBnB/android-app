package com.example.bookbnb.ui.busqueda

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentBusquedaLocationBinding
import com.example.bookbnb.models.TipoDeAlojamientoProvider
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.BusquedaViewModel
import com.example.bookbnb.viewmodels.BusquedaViewModelFactory
import com.google.android.material.slider.LabelFormatter.LABEL_GONE
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class BusquedaLocationFragment : BaseFragment() {

    companion object {
        private const val MIN_CHARS_TO_SUGGEST_LOCATION = 3
    }
    private val viewModel: BusquedaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, BusquedaViewModelFactory(activity.application))
            .get(BusquedaViewModel::class.java)
    }

    private lateinit var binding: FragmentBusquedaLocationBinding

    private var tiposAlojamientoAdapter: ArrayAdapter<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_busqueda_location,
            container,
            false
        )

        binding.busquedaViewModel = viewModel
        binding.lifecycleOwner = this

        setLocationTextObserver()
        setSuggestionOnClick()

        onNavigateToSearchResults()
        setPossibleTiposAlojamiento()
        setSnackbarMessageObserver(viewModel, binding.root)

        binding.numberPicker.value = viewModel.selectedCantHuespedes.value!!
        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.setSelectedCantHuespedes(newVal)
        }

        setPriceSlider()

        return binding.root
    }

    private fun setPriceSlider() {
        binding.rangeSlider.labelBehavior = LABEL_GONE
        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            viewModel.updateSelectedPrice(slider.values)
        }
    }

    private fun setPossibleTiposAlojamiento() {
        val tipos = TipoDeAlojamientoProvider.tipos.toMutableList()
        tipos.add(0, "Todos")
        if (tiposAlojamientoAdapter == null) {
            tiposAlojamientoAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tipos)
        }
        (binding.tipoAlojamientoType as? AutoCompleteTextView)?.setAdapter(tiposAlojamientoAdapter)
        viewModel.selectedTipoAlojamiento.value = if (viewModel.selectedTipoAlojamiento.value != null) viewModel.selectedTipoAlojamiento.value else tipos[0]
        (binding.tipoAlojamientoType as? AutoCompleteTextView)?.setText(viewModel.selectedTipoAlojamiento.value, false) //Defaults to first item in tipos
    }

    private fun onNavigateToSearchResults() {
        viewModel.navigateToSearchResults.observe(viewLifecycleOwner, Observer {
            if (it && viewModel.coordenadas.value != null) {
                NavHostFragment.findNavController(this).navigate(
                    BusquedaLocationFragmentDirections.actionBusquedaLocationFragmentToResultadosBusquedaFragment(
                        viewModel.coordenadas.value!!,
                        viewModel.selectedTipoAlojamiento.value!!,
                        viewModel.selectedCantHuespedes.value!!,
                        viewModel.selectedMinPrice.value!!,
                        viewModel.selectedMaxPrice.value!!
                    )
                )
                viewModel.onDoneNavigateToSearchResults()
            }
        })
    }

    private fun setSuggestionOnClick() {
        binding.searchText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                val item = viewModel.autocompleteLocationAdapter.value?.getItem(pos)
                viewModel.setSelectedLocation(item)
                binding.searchText.setSelection(0) //Focus on the start of the TextView
                hideKeyboard()
            }
    }

    private fun setLocationTextObserver() {
        viewModel.destino.observe(viewLifecycleOwner, Observer { location ->
            if (location.length >= MIN_CHARS_TO_SUGGEST_LOCATION) {
                viewModel.searchLocation(location)
            }
        })
    }
}

