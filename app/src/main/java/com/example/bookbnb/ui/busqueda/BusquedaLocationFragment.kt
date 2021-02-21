package com.example.bookbnb.ui.busqueda

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.LabelFormatter.LABEL_GONE
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import java.util.*

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

        removeDatesInputListeners()

        setLocationTextObserver()
        setSuggestionOnClick()
        setDatePickerObserver()

        onNavigateToSearchResults()
        setPossibleTiposAlojamiento()
        setSnackbarMessageObserver(viewModel, binding.root)

        binding.numberPicker.value = viewModel.selectedCantHuespedes.value!!
        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.setSelectedCantHuespedes(newVal)
        }

        setPriceSlider()
        viewModel.resetResults()

        return binding.root
    }

    private fun removeDatesInputListeners() {
        // We do this so binding can trigger datepicker and user is not able to write in the textboxes
        binding.checkinDate.inputType = InputType.TYPE_NULL
        binding.checkinDate.keyListener = null
        binding.checkinTextField.setEndIconOnClickListener{
            binding.checkinDate.text?.clear()
            binding.checkoutDate.text?.clear()
            viewModel.setDisponibilidadElegida(null, null)
        }
        binding.checkoutDate.inputType = InputType.TYPE_NULL
        binding.checkoutDate.keyListener = null
        binding.checkoutTextField.setEndIconOnClickListener{
            binding.checkinDate.text?.clear()
            binding.checkoutDate.text?.clear()
            viewModel.setDisponibilidadElegida(null, null)
        }
    }

    private fun setDatePickerObserver() {
        viewModel.displayDatePickerDialog.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = MaterialDatePicker.Builder.dateRangePicker()
                builder.setTitleText(getString(R.string.seleccion_fechas_text))
                val calendarConstraintsBuilder = CalendarConstraints.Builder()
                calendarConstraintsBuilder.setValidator(DateValidatorPointForward.now())
                builder.setCalendarConstraints(calendarConstraintsBuilder.build())
                if (viewModel.startDate.value != null && viewModel.endDate.value != null) {
                    builder.setSelection(androidx.core.util.Pair(viewModel.startDate.value?.time, viewModel.endDate.value?.time))
                }
                val picker = builder.build()
                picker.show(childFragmentManager, picker.toString())
                picker.addOnPositiveButtonClickListener {
                    if (it.first != null && it.second != null) {
                        viewModel.setDisponibilidadElegida(it.first!!, it.second!!)
                    }
                }
                viewModel.onDoneDisplayingDatePickerDialog()
            }
        })
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
            if (it && viewModel.selectedLocation.value != null) {
                NavHostFragment.findNavController(this).navigate(
                    BusquedaLocationFragmentDirections.actionBusquedaLocationFragmentToResultadosBusquedaFragment()
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

